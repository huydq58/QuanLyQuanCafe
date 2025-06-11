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

    /**
     * Lấy danh sách nguyên liệu sắp hết (số lượng tồn <= ngưỡng cảnh báo).
     * Phục vụ Use Case: [UC007] Xem Cảnh Báo Nguyên Liệu Sắp Hết.
     */
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

    /**
     * Cập nhật (trừ) số lượng tồn kho của một nguyên liệu.
     * Phục vụ Use Case: [UC011] Xác Nhận Thanh Toán Đơn Hàng (tự động trừ kho).
     * @param idNguyenLieu ID của nguyên liệu cần cập nhật.
     * @param luongGiam Số lượng cần trừ đi.
     * @return true nếu cập nhật thành công, false nếu thất bại.
     */
    public boolean capNhatSoLuongTon(int idNguyenLieu, double luongGiam) {
        String sql = "UPDATE NguyenLieu SET soLuongTon = soLuongTon - ? WHERE id = ?";
        return dataProvider.executeUpdate(sql, luongGiam, idNguyenLieu) > 0;
    }

    // Hàm tiện ích để chuyển đổi dữ liệu từ ResultSet sang đối tượng NguyenLieu
    private NguyenLieu mapResultSetToNguyenLieu(ResultSet rs) throws SQLException {
        NguyenLieu nl = new NguyenLieu();
        nl.setId(rs.getInt("id"));
        nl.setTen(rs.getString("ten"));
        nl.setDonViTinh(rs.getString("donViTinh"));
        nl.setSoLuongTon(rs.getDouble("soLuongTon"));
        nl.setNguongCanhBao(rs.getDouble("nguongCanhBao"));
        return nl;
    }
    
    // Các hàm CRUD cơ bản sẽ cần cho Tab Quản lý kho sau này
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
}