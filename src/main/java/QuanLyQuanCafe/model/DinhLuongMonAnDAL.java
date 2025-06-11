package QuanLyQuanCafe.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import QuanLyQuanCafe.database.DataProvider;

public class DinhLuongMonAnDAL {
    private final DataProvider dataProvider;

    public DinhLuongMonAnDAL(DataProvider provider) {
        this.dataProvider = provider;
    }

    /**
     * Lấy danh sách nguyên liệu và định lượng (công thức) cho một món ăn cụ thể.
     * Phục vụ Use Case: [UC011] Xác Nhận Thanh Toán Đơn Hàng (để biết cần trừ những gì).
     * @param idMonAn ID của món ăn cần lấy công thức.
     * @return Danh sách các định lượng cho món ăn đó.
     */
    public List<DinhLuongMonAn> getDinhLuongByMonAnId(int idMonAn) {
        List<DinhLuongMonAn> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM DinhLuongMonAn WHERE idMonAn = ?";
        try (Connection conn = dataProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idMonAn);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(mapResultSetToDinhLuong(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    // Hàm tiện ích để chuyển đổi dữ liệu từ ResultSet sang đối tượng DinhLuongMonAn
    private DinhLuongMonAn mapResultSetToDinhLuong(ResultSet rs) throws SQLException {
        DinhLuongMonAn dl = new DinhLuongMonAn();
        dl.setIdMonAn(rs.getInt("idMonAn"));
        dl.setIdNguyenLieu(rs.getInt("idNguyenLieu"));
        dl.setSoLuongCan(rs.getDouble("soLuongCan"));
        return dl;
    }
    
    // Các hàm thêm/xóa công thức sẽ cần cho Tab Quản lý Món ăn sau này
}