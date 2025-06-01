package QuanLyQuanCafe.database;

import java.sql.*;

public class DataProvider {
    private final String connectionString;

    // Constructor có thể nhận trực tiếp connection string hoặc dùng từ config class
    public DataProvider() {
        // Cách 1: dùng lớp DatabaseConfig (nếu bạn có)
        this.connectionString = DatabaseConfig.getConnectionString();

        // Cách 2: hoặc hard-code tạm thời (nếu chưa có DatabaseConfig):
        // this.connectionString = "jdbc:sqlserver://localhost:1433;databaseName=MiniSupermarket;user=sa;password=your_password";

        if (this.connectionString == null || this.connectionString.isEmpty()) {
            throw new IllegalStateException("Chuỗi kết nối chưa được cấu hình.");
        }

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver"); // nạp driver JDBC
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionString);
    }

    public boolean checkConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ResultSet executeQuery(String sql, Object... params) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        setParameters(stmt, params);
        return stmt.executeQuery(); // Đừng quên đóng conn và stmt sau khi sử dụng
    }


    public int executeNonQuery(String query, Object[] params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            setParameters(stmt, params);
            return stmt.executeUpdate();
        }
    }

    public Object executeScalar(String query, Object[] params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            setParameters(stmt, params);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getObject(1);
            }
            return null;
        }
    }
    public int executeUpdate(String sql, Object... params) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setParameters(stmt, params);

            return stmt.executeUpdate();  // Trả về số dòng ảnh hưởng

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;  // Báo lỗi
        }
    }
    // Hàm trả về ID khi INSERT (dành cho insertBill chẳng hạn)
    public int executeUpdateWithIdentity(String sql, Object... params) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setParameters(stmt, params);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);  // Trả về ID vừa thêm
                }
            }
            return -1;

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    private void setParameters(PreparedStatement stmt, Object[] params) throws SQLException {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
        }
    }
}