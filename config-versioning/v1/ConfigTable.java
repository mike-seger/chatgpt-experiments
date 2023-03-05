public class ConfigTable1DAO {
    private static final String SELECT_ALL_SQL = "SELECT * FROM config_table1 WHERE version_id = ? AND is_active = TRUE";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM config_table1 WHERE id = ?";
    private static final String INSERT_SQL = "INSERT INTO config_table1 (name, value, version_id, is_active) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE config_table1 SET name = ?, value = ?, is_active = ? WHERE id = ?";
    private static final String DELETE_SQL = "DELETE FROM config_table1 WHERE id = ?";
    
    private final DataSource dataSource;
    
    public ConfigTable1DAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public List<ConfigItem> getAll(int versionId) {
        List<ConfigItem> configItems = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL)) {
            stmt.setInt(1, versionId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                ConfigItem configItem = new ConfigItem();
                configItem.setId(rs.getInt("id"));
                configItem.setName(rs.getString("name"));
                configItem.setValue(rs.getString("value"));
                configItem.setVersionId(rs.getInt("version_id"));
                configItem.setActive(rs.getBoolean("is_active"));
                configItems.add(configItem);
            }
        } catch (SQLException e) {
            // handle exception
        }
        
        return configItems;
    }
    
    public ConfigItem getById(int id) {
        ConfigItem configItem = null;
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                configItem = new ConfigItem();
                configItem.setId(rs.getInt("id"));
                configItem.setName(rs.getString("name"));
                configItem.setValue(rs.getString("value"));
                configItem.setVersionId(rs.getInt("version_id"));
                configItem.setActive(rs.getBoolean("is_active"));
            }
        } catch (SQLException e) {
            // handle exception
        }
        
        return configItem;
    }
    
    public void insert(ConfigItem configItem) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {
            stmt.setString(1, configItem.getName());
            stmt.setString(2, configItem.getValue());
            stmt.setInt(3, configItem.getVersionId());
            stmt.setBoolean(4, configItem.isActive());
            stmt.executeUpdate();
        } catch (SQLException e) {
            // handle exception
        }
    }
    
    public void update(ConfigItem configItem) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, configItem.getName());
            stmt.setString(2, configItem.getValue());
            stmt.setBoolean(3, configItem.isActive());
            stmt.setInt(4, configItem.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            // handle exception
        }
    }
    
    public void delete(int id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            // handle exception
        }
    }
}
       
