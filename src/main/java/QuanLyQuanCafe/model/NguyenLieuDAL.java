package QuanLyQuanCafe.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import QuanLyQuanCafe.database.DataProvider;

public class NguyenLieuDAL {
    private final DataProvider dataProvider;

    public NguyenLieuDAL(DataProvider provider) {
        this.dataProvider = provider;
    }
    
    // Lấy tất cả nguyên liệu
    public List<NguyenLieu> getAllNguyenLieu() {
        List<NguyenLieu> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM NguyenLieu";
        try (Connection conn = dataProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                danhSach.add(mapResultSetToNguyenLieu(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }
    
    // PHƯƠNG THỨC BỊ THIẾU GÂY LỖI
    public List<NguyenLieu> getNguyenLieuDuoiNguong() {
        List<NguyenLieu> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM NguyenLieu WHERE soLuongTon <= nguongCanhBao AND nguongCanhBao > 0";
        try (Connection conn = dataProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                danhSach.add(mapResultSetToNguyenLieu(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }
    // Lấy nguyên liệu theo ID

    public NguyenLieu getNguyenLieuById(int id) {
        String sql = "SELECT * FROM NguyenLieu WHERE id = ?";
        try (Connection conn = dataProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToNguyenLieu(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Thêm nguyên liệu mới
    public int addNguyenLieu(NguyenLieu nl) {
        String sql = "INSERT INTO NguyenLieu (ten, donViTinh, soLuongTon, nguongCanhBao) VALUES (?, ?, ?, ?)";
        return dataProvider.executeUpdate(sql, nl.getTen(), nl.getDonViTinh(), nl.getSoLuongTon(), nl.getNguongCanhBao());
    }

    // Cập nhật nguyên liệu
    public int updateNguyenLieu(NguyenLieu nl) {
        String sql = "UPDATE NguyenLieu SET ten = ?, donViTinh = ?, soLuongTon = ?, nguongCanhBao = ? WHERE id = ?";
        return dataProvider.executeUpdate(sql, nl.getTen(), nl.getDonViTinh(), nl.getSoLuongTon(), nl.getNguongCanhBao(), nl.getId());
    }

    // Xóa nguyên liệu
    public int deleteNguyenLieu(int id) {
        String sql = "DELETE FROM NguyenLieu WHERE id = ?";
        return dataProvider.executeUpdate(sql, id);
    }
    
    // Cập nhật (trừ) số lượng tồn kho khi thanh toán
    public boolean capNhatSoLuongTon(int idNguyenLieu, double luongGiam) {
        String sql = "UPDATE NguyenLieu SET soLuongTon = soLuongTon - ? WHERE id = ?";
        return dataProvider.executeUpdate(sql, luongGiam, idNguyenLieu) > 0;
    }

    private NguyenLieu mapResultSetToNguyenLieu(ResultSet rs) throws SQLException {
        NguyenLieu nl = new NguyenLieu();
        nl.setId(rs.getInt("id"));
        nl.setTen(rs.getString("ten"));
        nl.setDonViTinh(rs.getString("donViTinh"));
        nl.setSoLuongTon(rs.getDouble("soLuongTon"));
        nl.setNguongCanhBao(rs.getDouble("nguongCanhBao"));
        return nl;
    }
}