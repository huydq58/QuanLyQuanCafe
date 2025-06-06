package QuanLyQuanCafe.model;

import QuanLyQuanCafe.database.DataProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FoodDAL {
    private final DataProvider dataProvider;

    public FoodDAL(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    // Lấy tất cả món ăn
    public List<Food> getAllFood() {
        List<Food> list = new ArrayList<>();
        String query = "SELECT * FROM Food";

        try (Connection conn = dataProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Food food = new Food();
                food.setId(rs.getInt("id"));
                food.setName(rs.getString("name"));
                food.setCategoryId(rs.getInt("categoryId"));
                food.setPrice(rs.getLong("price"));
                food.setUnit(rs.getString("unit"));
                food.setDescription(rs.getString("description"));
                food.setImagePath(rs.getString("imagePath"));
                food.setAvailable(rs.getBoolean("isAvailable"));
                list.add(food);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Thêm món ăn mới
    public int addFood(Food food) {
        String query = "INSERT INTO Food (name, categoryId, price, unit, description, isAvailable, imagePath) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, food.getName());
            stmt.setInt(2, food.getCategoryId());
            stmt.setLong(3, food.getPrice());
            stmt.setString(4, food.getUnit());
            stmt.setString(5, food.getDescription());
            stmt.setBoolean(6, food.isAvailable());
            stmt.setString(7, food.getImagePath());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    food.setId(keys.getInt(1));
                }
            }
            return affectedRows;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Cập nhật món ăn
    public int updateFood(Food food) {
        String query = "UPDATE Food SET name = ?, categoryId = ?, price = ?, unit = ?, description = ?, isAvailable = ?, imagePath = ? WHERE id = ?";
        try (Connection conn = dataProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, food.getName());
            stmt.setInt(2, food.getCategoryId());
            stmt.setLong(3, food.getPrice());
            stmt.setString(4, food.getUnit());
            stmt.setString(5, food.getDescription());
            stmt.setBoolean(6, food.isAvailable());
            stmt.setString(7, food.getImagePath());
            stmt.setInt(8, food.getId());

            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Xóa món ăn theo id
    public int deleteFood(int id) {
        String query = "DELETE FROM Food WHERE id = ?";
        try (Connection conn = dataProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Lấy món ăn theo id
    public Food getFoodById(int id) {
        String query = "SELECT * FROM Food WHERE id = ?";
        try (Connection conn = dataProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Food food = new Food();
                food.setId(rs.getInt("id"));
                food.setName(rs.getString("name"));
                food.setCategoryId(rs.getInt("categoryId"));
                food.setPrice(rs.getLong("price"));
                food.setUnit(rs.getString("unit"));
                food.setDescription(rs.getString("description"));
                food.setImagePath(rs.getString("imagePath"));
                food.setAvailable(rs.getBoolean("isAvailable"));
                return food;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
