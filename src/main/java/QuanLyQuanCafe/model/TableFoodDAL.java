package QuanLyQuanCafe.model;

import QuanLyQuanCafe.database.DataProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TableFoodDAL {
    private final DataProvider dataProvider;

    public TableFoodDAL(DataProvider provider) {
        this.dataProvider = provider;
    }

    public List<TableFood> getAllTables() {
        List<TableFood> list = new ArrayList<>();
        String query = "SELECT * FROM TableFood";

        try (Connection conn = dataProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                TableFood table = new TableFood();
                table.setId(rs.getInt("id"));
                table.setName(rs.getString("name"));
                table.setAvailable(rs.getBoolean("isAvailable"));

                list.add(table);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public TableFood getTableById(int id) {
        String query = "SELECT * FROM TableFood WHERE id = ?";
        try (Connection conn = dataProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new TableFood(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getBoolean("isAvailable")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public int addTable(TableFood table) {
        String query = "INSERT INTO TableFood (name, isAvailable) VALUES (?, ?)";
        try (Connection conn = dataProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, table.getName());
            stmt.setBoolean(2, table.isAvailable());

            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int updateTable(TableFood table) {
        String query = "UPDATE TableFood SET name = ?, isAvailable = ? WHERE id = ?";
        try (Connection conn = dataProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, table.getName());
            stmt.setBoolean(2, table.isAvailable());
            stmt.setInt(3, table.getId());

            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int deleteTable(int id) {
        String query = "DELETE FROM TableFood WHERE id = ?";
        try (Connection conn = dataProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int getTableCount() {
        String query = "SELECT COUNT(*) FROM TableFood";


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

