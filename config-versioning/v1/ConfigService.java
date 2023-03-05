public class ConfigService {
    
    public void switchVersion(int versionId) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            
            // set all rows with the old version_id to inactive
            PreparedStatement stmt = conn.prepareStatement("UPDATE config_table1 SET is_active = FALSE WHERE version_id = ? AND is_active = TRUE");
            stmt.setInt(1, getCurrentVersionId(conn));
            stmt.executeUpdate();
            
            stmt = conn.prepareStatement("UPDATE config_table2 SET is_active = FALSE WHERE version_id = ? AND is_active = TRUE");
            stmt.setInt(1, getCurrentVersionId(conn));
            stmt.executeUpdate();
            
            stmt = conn.prepareStatement("UPDATE config_table3 SET is_active = FALSE WHERE version_id = ? AND is_active = TRUE");
            stmt.setInt(1, getCurrentVersionId(conn));
            stmt.executeUpdate();
            
            // set all rows with the new version_id to active
            stmt = conn.prepareStatement("UPDATE config_table1 SET is_active = TRUE WHERE version_id = ?");
            stmt.setInt(1, versionId);
            stmt.executeUpdate();
            
            stmt = conn.prepareStatement("UPDATE config_table2 SET is_active = TRUE WHERE version_id = ?");
            stmt.setInt(1, versionId);
            stmt.executeUpdate();
            
            stmt = conn.prepareStatement("UPDATE config_table3 SET is_active = TRUE WHERE version_id = ?");
            stmt.setInt(1, versionId);
            stmt.executeUpdate();
            
            // update the current version_id in the version table
            stmt = conn.prepareStatement("UPDATE version SET timestamp = CURRENT_TIMESTAMP WHERE id = ?");
            stmt.setInt(1, versionId);
            stmt.executeUpdate();
            
            conn.commit();
        } catch (SQLException e) {
            // handle exception
        }
    }
    
    public int getCurrentVersionId(Connection conn) throws SQLException {
        int currentVersionId = 0;
        
        try (PreparedStatement stmt = conn.prepareStatement("SELECT id FROM version ORDER BY timestamp DESC LIMIT 1");
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                currentVersionId = rs.getInt("id");
            }
        }
        
        return currentVersionId;
    }
}
