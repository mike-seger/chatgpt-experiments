import java.io.IOException;
import java.io.InputStream;
import java.sql.*;

public class BlobQuery {

    public static void main(String[] args) throws SQLException, IOException {
        // Replace the connection details with your own
        String url = "jdbc:oracle:thin:@//localhost:1521/xe";
        String username = "your_username";
        String password = "your_password";

        // Create a connection
        Connection conn = DriverManager.getConnection(url, username, password);

        // Replace the query with your own
        String query = "SELECT blob_column FROM your_table WHERE your_condition";

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
    }
}
