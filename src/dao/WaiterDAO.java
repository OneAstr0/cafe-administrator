package dao;

import model.Waiter;
import db.DBConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WaiterDAO {

    public List<Waiter> getAllWaiters() {
        List<Waiter> waiters = new ArrayList<>();
        String sql = "SELECT id, full_name, phone, shift FROM waiters";

        try (Connection conn = DBConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Waiter waiter = new Waiter(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("phone"),
                        rs.getString("shift")
                );
                waiters.add(waiter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return waiters;
    }
}