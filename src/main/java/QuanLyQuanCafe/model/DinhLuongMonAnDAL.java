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

    /**
     * Ghi đè toàn bộ công thức cho một món ăn.
     * @param idMonAn ID của món ăn cần cập nhật công thức.
     * @param newRecipe Danh sách các định lượng mới.
     */
    public void overwriteDinhLuongForMonAn(int idMonAn, List<DinhLuongMonAn> newRecipe) {
        String deleteSql = "DELETE FROM DinhLuongMonAn WHERE idMonAn = ?";
        String insertSql = "INSERT INTO DinhLuongMonAn (idMonAn, idNguyenLieu, soLuongCan) VALUES (?, ?, ?)";
        
        try (Connection conn = dataProvider.getConnection()) {
            // Tắt auto-commit để thực hiện giao dịch
            conn.setAutoCommit(false);
            
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                // Xóa hết công thức cũ
                deleteStmt.setInt(1, idMonAn);
                deleteStmt.executeUpdate();
                
                // Thêm lại các định lượng mới
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    for (DinhLuongMonAn dl : newRecipe) {
                        insertStmt.setInt(1, idMonAn);
                        insertStmt.setInt(2, dl.getIdNguyenLieu());
                        insertStmt.setDouble(3, dl.getSoLuongCan());
                        insertStmt.addBatch();
                    }
                    insertStmt.executeBatch();
                }
                
                // Commit giao dịch nếu tất cả thành công
                conn.commit();
            } catch (SQLException e) {
                // Nếu có lỗi, rollback lại
                conn.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private DinhLuongMonAn mapResultSetToDinhLuong(ResultSet rs) throws SQLException {
        DinhLuongMonAn dl = new DinhLuongMonAn();
        dl.setIdMonAn(rs.getInt("idMonAn"));
        dl.setIdNguyenLieu(rs.getInt("idNguyenLieu"));
        dl.setSoLuongCan(rs.getDouble("soLuongCan"));
        return dl;
    }
}