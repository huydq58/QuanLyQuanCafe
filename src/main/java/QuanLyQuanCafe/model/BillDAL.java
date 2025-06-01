package QuanLyQuanCafe.model;

import QuanLyQuanCafe.database.DataProvider;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BillDAL {
    private final DataProvider dataProvider;

    public BillDAL(DataProvider provider) {
        this.dataProvider = provider;
    }

    public List<Bill> getAllBills() {
        List<Bill> bills = new ArrayList<>();
        String query = "SELECT * FROM Bill";

        try (Connection conn = dataProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Bill bill = mapResultSetToBill(rs);
                bills.add(bill);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bills;
    }

    public Bill getBillById(int id) {
        String query = "SELECT * FROM Bill WHERE id = ?";
        try (Connection conn = dataProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToBill(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public int insertBill(Bill bill) {
        String sql = "INSERT INTO Bill(tableID, disCount, paid, orderDate, totalPrice) VALUES (?, ?, ?, ?, ?)";
        int result = dataProvider.executeUpdateWithIdentity(sql,
                bill.getTableID(),
                bill.getDisCount(),
                bill.isPaid(),
                bill.getOrderDate(),
                bill.getTotalPrice()
        );
        return result; // trả về ID vừa insert
    }

    public boolean updateBill(Bill bill) {
        String query = "UPDATE Bill SET tableID = ?, disCount = ?, paid = ?, orderDate = ?, paidDate = ?, totalPrice = ? WHERE id = ?";
        try (Connection conn = dataProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, bill.getTableID());
            stmt.setInt(2, bill.getDisCount());
            stmt.setBoolean(3, bill.isPaid());
            stmt.setTimestamp(4, Timestamp.valueOf(bill.getOrderDate()));

            if (bill.getPaidDate() != null) {
                stmt.setTimestamp(5, Timestamp.valueOf(bill.getPaidDate()));
            } else {
                stmt.setNull(5, Types.TIMESTAMP);
            }

            stmt.setDouble(6, bill.getTotalPrice());
            stmt.setInt(7, bill.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteBill(int id) {
        String query = "DELETE FROM Bill WHERE id = ?";
        try (Connection conn = dataProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private Bill mapResultSetToBill(ResultSet rs) throws SQLException {
        Bill bill = new Bill();
        bill.setId(rs.getInt("id"));
        bill.setTableID(rs.getInt("tableID"));
        bill.setDisCount(rs.getInt("disCount"));
        bill.setPaid(rs.getBoolean("paid"));
        bill.setOrderDate(rs.getTimestamp("orderDate").toLocalDateTime());

        Timestamp paidTs = rs.getTimestamp("paidDate");
        if (paidTs != null) {
            bill.setPaidDate(paidTs.toLocalDateTime());
        }

        bill.setTotalPrice(rs.getDouble("totalPrice"));
        return bill;
    }


}
