import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Properties;
import java.util.Scanner;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class DbQuery {
	private final static Properties dbProperties = new Properties();
	private static String dbp(String name) { return dbProperties.get(name)+""; }
	
	public static void main(String[] args) {
		try {
			File dbPropertiesFile = new File("db.properties");
			if (!dbPropertiesFile.exists())
				throw new IllegalArgumentException("A db.properties file has not been found");
			try (FileReader fr = new FileReader(dbPropertiesFile)) { dbProperties.load(fr);	}
			if (args.length != 1)
				throw new IllegalArgumentException("You must provide a query");
			String query = args[0];
			setSystemSSLContext(dbp("truststorePath"), dbp("truststorePassword"),
					dbp("keystorePath"), dbp("keystorePassword"));

			Class.forName(dbp("driverClass"));
			try (
				Connection conn = DriverManager.getConnection(dbp("url"), dbp("username"), dbp("password"));
				PreparedStatement stmt = conn.prepareStatement(query);
				ResultSet rs = stmt.executeQuery()) {
					ResultSetMetaData metaData = rs.getMetaData();
					int columnCount = metaData.getColumnCount();
					for (int i = 1; i <= columnCount; i++) {
						System.out.print(metaData.getColumnName(i));
						System.out.print("\t");
					}
					System.out.println();
					while (rs.next()) {
						for (int i = 1; i <= columnCount; i++) {
							if (metaData.getColumnType(i) == java.sql.Types.BLOB) {
								try (InputStream is = rs.getBinaryStream(i)) {
									Scanner scanner = new Scanner(is).useDelimiter("\\A");
									String s = scanner.hasNext() ? scanner.next() : "";
									s = s.replace("\r", "\\r")
										.replace("\n", "\\n")
										.replace("\t", "\\t");
									System.out.print(s);
									System.out.print('\t');
								}
							} else {
								System.out.print(rs.getString(i) + "\t");
							}
						}
						System.out.println();
					}
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void setSystemSSLContext(String truststorePath, String truststorePassword, String keystorePath, String keystorePassword) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, KeyManagementException, UnrecoverableKeyException {
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

		// Set the SSL context on the default SSL socket factory
		javax.net.ssl.SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
		System.setProperty("javax.net.ssl.keyStore", keystorePath);
		System.setProperty("javax.net.ssl.keyStorePassword", keystorePassword);
		System.setProperty("javax.net.ssl.trustStore", truststorePath);
		System.setProperty("javax.net.ssl.trustStorePassword", truststorePassword);
		System.setProperty("javax.net.ssl.SSLSocketFactory", sslSocketFactory.getClass().getName());
	}
}
