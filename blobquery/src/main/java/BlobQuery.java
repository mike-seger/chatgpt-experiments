import oracle.jdbc.datasource.OracleDataSource;
import oracle.jdbc.pool.OracleConnectionPoolDataSource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.*;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class BlobQuery {

	public static void main(String[] args) {
		// Replace the connection details with your own
		String url = "jdbc:oracle:thin:@//localhost:1521/xe";
//		String username = "system";
//		String password = "oracle";

//		String url = "jdbc:oracle:thin:@localhost:1521/ORCLPDB1";
		String username = "myuser";
		String password = "mypassword";

		String truststorePath = "./jks/truststore.jks";
		String truststorePassword = "secure";
		String keystorePath = "./jks/keystore.jks";
		String keystorePassword = "secure";

		try {
			if(args.length!=1) throw new IllegalArgumentException("You must provide a query");
			String query = args[0];
			// Load the truststore
			KeyStore truststore = KeyStore.getInstance("JKS");
			try (InputStream truststoreInputStream = new FileInputStream(truststorePath)) {
				truststore.load(truststoreInputStream, truststorePassword.toCharArray());
			}
			TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init(truststore);

			// Load the keystore
			KeyStore keystore = KeyStore.getInstance("JKS");
			try (InputStream keystoreInputStream = new FileInputStream(keystorePath)) {
				keystore.load(keystoreInputStream, keystorePassword.toCharArray());
			}
			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(keystore, keystorePassword.toCharArray());

			// Create an SSL context
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

			// Set the SSL context on the Oracle JDBC driver
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			OracleConnectionPoolDataSource ds = new OracleConnectionPoolDataSource();
			ds.setURL(url);
			ds.setUser(username);
			ds.setPassword(password);
			ds.setSSLContext(sslContext);
			Connection conn = ds.getConnection();

			// Create a statement
			PreparedStatement stmt = conn.prepareStatement(query);

			// Execute the query
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				// Get the BLOB column as an InputStream
				InputStream is = rs.getBinaryStream(1);

				// Create a buffer to hold the BLOB data
				byte[] buffer = new byte[1024];

				// Read the BLOB data into the buffer
				int bytesRead;
				while ((bytesRead = is.read(buffer)) != -1) {
					// Write the BLOB data to stdout
					System.out.write(buffer, 0, bytesRead);
				}

				// Close the InputStream
				is.close();
			}

			// Close the ResultSet, statement, and connection
			rs.close();
			stmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
