package QuanLyQuanCafe.database;

public class DatabaseConfig {
    public static String getConnectionString() {
        return "jdbc:sqlserver://localhost:1433;databaseName=QuanLyQuanCafe;encrypt=false;trustServerCertificate=true;user=sa;password=1234";
    }
}
