package dao;

import model.Table;
import db.DBConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TableDAO {

    // Получить все столы
    public List<Table> getAllTables() {
        List<Table> tables = new ArrayList<>();
        String sql = "SELECT id, table_number, seats, status FROM tables ORDER BY table_number";

        try (Connection conn = DBConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Table table = new Table(
                        rs.getInt("id"),
                        rs.getInt("table_number"),
                        rs.getInt("seats"),
                        rs.getString("status")
                );
                tables.add(table);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tables;
    }

    // Обновить статус стола
    public boolean updateTableStatus(int tableId, String status) {
        String sql = "UPDATE tables SET status = ? WHERE id = ?";

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, tableId);
            return pstmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Получить стол по ID
    public Table getTableById(int id) {
        String sql = "SELECT id, table_number, seats, status FROM tables WHERE id = ?";

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Table(
                        rs.getInt("id"),
                        rs.getInt("table_number"),
                        rs.getInt("seats"),
                        rs.getString("status")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}