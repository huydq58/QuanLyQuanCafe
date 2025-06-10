package QuanLyQuanCafe.model;

import QuanLyQuanCafe.database.DataProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAL {
    private final DataProvider dataProvider;

    public CategoryDAL(DataProvider provider) {
        this.dataProvider = provider;
    }

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String query = "SELECT id, name FROM Category WHERE isActive = 1";

        try (Connection conn = dataProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Category category = new Category();
                category.setId(rs.getInt("id"));
                category.setName(rs.getString("name"));
                categories.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categories;
    }

    public Category getCategoryById(int id) {
        String query = "SELECT id, name FROM Category WHERE id = ?";
        try (Connection conn = dataProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Category(rs.getInt("id"), rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean addCategory(Category category) {
        String query = "INSERT INTO Category (name, description, isActive) VALUES (?, NULL, 1)";
        try (Connection conn = dataProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, category.getName());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateCategory(Category category) {
        String query = "UPDATE Category SET name = ? WHERE id = ?";
        try (Connection conn = dataProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, category.getName());
            stmt.setInt(2, category.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteCategory(int id) {
        String query = "UPDATE Category SET isActive = 0 WHERE id = ?";
        try (Connection conn = dataProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public int getCategoryCount() {
        String query = "SELECT COUNT(*) FROM Category";


        try (Connection conn = dataProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {


            if (rs.next()) {

                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        return 0;
    }
}
