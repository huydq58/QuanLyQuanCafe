package QuanLyQuanCafe.database;
import java.sql.*;
import QuanLyQuanCafe.*;
import QuanLyQuanCafe.crypto.Hashing;

public class sqlserver {
    public static void main(String[] args) {
        String url = "jdbc:sqlserver://localhost:1433;databaseName=QuanLyQuanCafe;encrypt=false";
        String user = "sa";
        String password = "1234";
//        String tendangnhap = "ad";
//        String passdangnhap = "1";
//        String hashedpassdangnhap = Hashing.hashPassword(passdangnhap);
//        try (Connection conn = DriverManager.getConnection(url, user, password)) {
//            System.out.println("Kết nối thành công!");

//            // Ví dụ: Truy vấn dữ liệu
//            String query = "INSERT INTO TaiKhoan (TenDangNhap, MatKhauHash, MaNV) VALUES (?, ?, ?)";
//            PreparedStatement pstmt = conn.prepareStatement(query);
////            pstmt.setString(1, tendangnhap);       // biến tenDangNhap (String)
////            pstmt.setString(2, hashedpassdangnhap); // biến hashedPassDangNhap (String)
////            pstmt.setInt(3, 1);                 // biến maNV (int hoặc Integer)

//            int result = pstmt.executeUpdate();
//            if (result > 0) {
//                System.out.println("Thêm tài khoản thành công.");
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }

}
