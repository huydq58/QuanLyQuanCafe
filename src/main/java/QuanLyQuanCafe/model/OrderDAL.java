package QuanLyQuanCafe.model;


import QuanLyQuanCafe.database.DataProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAL {
    private final DataProvider dataProvider;

    public OrderDAL(DataProvider provider) {
        this.dataProvider = provider;
    }

    public List<Order> getAllOrders() {
        List<Order> list = new ArrayList<>();
        String query = "SELECT * FROM [Order]";

        try (Connection conn = dataProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                list.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Order> getOrdersByBillID(int billId) {
        List<Order> list = new ArrayList<>();
        String query = "SELECT * FROM [Order] WHERE billID = ?";

        try (Connection conn = dataProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, billId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                list.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public int insertOrder(Order order) {
        String query = "INSERT INTO [Order] (billID, foodID, count) VALUES (?, ?, ?)";
        try (Connection conn = dataProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, order.getBillID());
            stmt.setInt(2, order.getFoodID());
            stmt.setLong(3, order.getCount());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public boolean updateOrder(Order order) {
        String query = "UPDATE [Order] SET billID = ?, foodID = ?, count = ? WHERE id = ?";
        try (Connection conn = dataProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, order.getBillID());
            stmt.setInt(2, order.getFoodID());
            stmt.setLong(3, order.getCount());
            stmt.setInt(4, order.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteOrder(int id) {
        String query = "DELETE FROM [Order] WHERE id = ?";
        try (Connection conn = dataProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getInt("id"));
        order.setBillID(rs.getInt("billID"));
        order.setFoodID(rs.getInt("foodID"));
        order.setCount(rs.getLong("count"));
        return order;
    }
    public boolean existsOrder(int billID, int foodID) throws SQLException {
        String sql = "SELECT COUNT(*) FROM [Order] WHERE billID = ? AND foodID = ?";
        ResultSet rs = dataProvider.executeQuery(sql, billID, foodID);
        try {
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean increaseQuantity(int billID, int foodID, long quantityToAdd) {
        String sql = "UPDATE [Order] SET count = count + ? WHERE billID = ? AND foodID = ?";
        return dataProvider.executeUpdate(sql, quantityToAdd, billID, foodID) > 0;
    }



}

