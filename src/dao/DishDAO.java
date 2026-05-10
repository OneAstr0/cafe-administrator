package dao;

import model.Dish;
import db.DBConnectionManager;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DishDAO {

    public List<Dish> getAllDishes() {
        List<Dish> dishes = new ArrayList<>();
        String sql = "SELECT id, name, category_id, price, weight_volume FROM dishes ORDER BY category_id, name";

        try (Connection conn = DBConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Dish dish = new Dish(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("category_id"),
                        rs.getBigDecimal("price"),
                        rs.getString("weight_volume")
                );
                dishes.add(dish);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dishes;
    }

    public Dish getDishById(int id) {
        String sql = "SELECT id, name, category_id, price, weight_volume FROM dishes WHERE id = ?";

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Dish(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("category_id"),
                        rs.getBigDecimal("price"),
                        rs.getString("weight_volume")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}