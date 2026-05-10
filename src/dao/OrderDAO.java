package dao;

import model.Order;
import db.DBConnectionManager;
import model.OrderItem;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    // Получить открытый заказ по ID стола
    public Order getOpenOrderByTableId(int tableId) {
        String sql = "SELECT id, table_id, waiter_id, opened_at, closed_at, total_sum, status " +
                "FROM orders WHERE table_id = ? AND status = 'open'";

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tableId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Order(
                        rs.getInt("id"),
                        rs.getInt("table_id"),
                        rs.getInt("waiter_id"),
                        rs.getTimestamp("opened_at"),
                        rs.getTimestamp("closed_at"),
                        rs.getBigDecimal("total_sum"),
                        rs.getString("status")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Создать новый заказ
    public int createOrder(int tableId, int waiterId) {
        String sql = "INSERT INTO orders (table_id, waiter_id, status) VALUES (?, ?, 'open') RETURNING id";

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tableId);
            pstmt.setInt(2, waiterId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Закрыть заказ (обновить статус и итоговую сумму)
    public boolean closeOrder(int orderId, BigDecimal totalSum) {
        String sql = "UPDATE orders SET status = 'closed', closed_at = CURRENT_TIMESTAMP, total_sum = ? WHERE id = ?";

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBigDecimal(1, totalSum);
            pstmt.setInt(2, orderId);
            return pstmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Добавить блюдо в заказ
    public boolean addOrderItem(int orderId, int dishId, int quantity, BigDecimal price) {
        String sql = "INSERT INTO order_items (order_id, dish_id, quantity, price_at_order) " +
                "VALUES (?, ?, ?, ?) " +
                "ON CONFLICT (order_id, dish_id) DO UPDATE SET quantity = order_items.quantity + ?";

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            pstmt.setInt(2, dishId);
            pstmt.setInt(3, quantity);
            pstmt.setBigDecimal(4, price);
            pstmt.setInt(5, quantity);
            return pstmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Получить все блюда из заказа
    public List<OrderItem> getOrderItems(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT oi.dish_id, oi.quantity, oi.price_at_order, d.name " +
                "FROM order_items oi JOIN dishes d ON oi.dish_id = d.id " +
                "WHERE oi.order_id = ?";

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                OrderItem item = new OrderItem(
                        orderId,
                        rs.getInt("dish_id"),
                        rs.getString("name"),
                        rs.getInt("quantity"),
                        rs.getBigDecimal("price_at_order")
                );
                items.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    // Удалить блюдо из заказа
    public boolean removeOrderItem(int orderId, int dishId) {
        String sql = "DELETE FROM order_items WHERE order_id = ? AND dish_id = ?";

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            pstmt.setInt(2, dishId);
            return pstmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Обновить количество блюда
    public boolean updateOrderItemQuantity(int orderId, int dishId, int quantity) {
        String sql = "UPDATE order_items SET quantity = ? WHERE order_id = ? AND dish_id = ?";

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, quantity);
            pstmt.setInt(2, orderId);
            pstmt.setInt(3, dishId);
            return pstmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}