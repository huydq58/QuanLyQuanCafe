package QuanLyQuanCafe.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import QuanLyQuanCafe.database.DataProvider;

public class TaiKhoanDAL {
    private final DataProvider dataProvider;

    public TaiKhoanDAL(DataProvider provider) {
        this.dataProvider = provider;
    }

    public List<TaiKhoan> getAllTaiKhoan() {
        List<TaiKhoan> list = new ArrayList<>();
        // Lấy tất cả các cột cần thiết, bao gồm cả Role
        String query = "SELECT TenDangNhap, MatKhauHash, MaNV, Role FROM TaiKhoan";

        try (Connection conn = dataProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                TaiKhoan tk = new TaiKhoan();
                tk.setTenDangNhap(rs.getString("TenDangNhap"));
                tk.setMatKhauHash(rs.getString("MatKhauHash"));
                tk.setRole(rs.getString("Role"));

                int maNV = rs.getInt("MaNV");
                tk.setMaNV(rs.wasNull() ? null : maNV);

                list.add(tk);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public TaiKhoan getTaiKhoanByUsername(String username) {
        // Lấy tất cả các cột cần thiết, bao gồm cả Role
        String query = "SELECT TenDangNhap, MatKhauHash, MaNV, Role FROM TaiKhoan WHERE TenDangNhap = ?";
        try (Connection conn = dataProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                TaiKhoan tk = new TaiKhoan();
                tk.setTenDangNhap(rs.getString("TenDangNhap"));
                tk.setMatKhauHash(rs.getString("MatKhauHash").trim());
                tk.setRole(rs.getString("Role"));
                int maNV = rs.getInt("MaNV");
                tk.setMaNV(rs.wasNull() ? null : maNV);
                return tk;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public int addTaiKhoan(TaiKhoan tk) {
        // === SỬA LỖI Ở ĐÂY: Thêm cột 'Role' vào câu lệnh INSERT ===
        String query = "INSERT INTO TaiKhoan (TenDangNhap, MatKhauHash, MaNV, Role) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, tk.getTenDangNhap());
            stmt.setString(2, tk.getMatKhauHash());
            if (tk.getMaNV() != null) {
                stmt.setInt(3, tk.getMaNV());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            // Thêm tham số cho Role
            stmt.setString(4, tk.getRole());

            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int updateTaiKhoan(TaiKhoan tk) {
        // Nếu mật khẩu không được cung cấp (chuỗi rỗng), chỉ cập nhật MaNV và Role
        if (tk.getMatKhauHash() == null || tk.getMatKhauHash().isEmpty()) {
            String query = "UPDATE TaiKhoan SET MaNV = ?, Role = ? WHERE TenDangNhap = ?";
            try (Connection conn = dataProvider.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                if (tk.getMaNV() != null) {
                    stmt.setInt(1, tk.getMaNV());
                } else {
                    stmt.setNull(1, Types.INTEGER);
                }
                stmt.setString(2, tk.getRole());
                stmt.setString(3, tk.getTenDangNhap());
                return stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            // Nếu có mật khẩu mới, cập nhật tất cả
            String query = "UPDATE TaiKhoan SET MatKhauHash = ?, MaNV = ?, Role = ? WHERE TenDangNhap = ?";
            try (Connection conn = dataProvider.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, tk.getMatKhauHash());
                if (tk.getMaNV() != null) {
                    stmt.setInt(2, tk.getMaNV());
                } else {
                    stmt.setNull(2, Types.INTEGER);
                }
                stmt.setString(3, tk.getRole());
                stmt.setString(4, tk.getTenDangNhap());
                return stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }


    public int deleteTaiKhoan(String tenDangNhap) {
        String query = "DELETE FROM TaiKhoan WHERE TenDangNhap = ?";
        try (Connection conn = dataProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, tenDangNhap);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    // Các hàm khác giữ nguyên
    public TaiKhoan getTaiKhoanByTenDangNhapAndPasswordHash(String tenDangNhap, String matKhauHash) {
        String query = "SELECT * FROM TaiKhoan WHERE TenDangNhap = ? AND MatKhauHash = ?";
        try (Connection conn = dataProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, tenDangNhap);
            stmt.setString(2, matKhauHash);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                TaiKhoan tk = new TaiKhoan();
                tk.setTenDangNhap(rs.getString("TenDangNhap"));
                tk.setMatKhauHash(rs.getString("MatKhauHash"));
                int maNV = rs.getInt("MaNV");
                tk.setMaNV(rs.wasNull() ? null : maNV);
                tk.setRole(rs.getString("Role")); // Thêm lấy Role
                return tk;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}