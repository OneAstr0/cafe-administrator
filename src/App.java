import db.DBConnectionManager;
import java.sql.Connection;
import dao.TableDAO;
import dao.OrderDAO;
import model.Table;
import model.Order;
import ui.MainFrame;

import java.util.List;


public class App {
    public static void main(String[] args) {
        try (Connection conn = DBConnectionManager.getConnection()) {
            System.out.println("✅ Подключено к PostgreSQL!");
            System.out.println("Версия БД: " + conn.getMetaData().getDatabaseProductVersion());
        } catch (Exception e) {
            System.err.println("❌ Ошибка: " + e.getMessage());
            e.printStackTrace();
        }

        TableDAO tableDAO = new TableDAO();
        OrderDAO orderDAO = new OrderDAO();

        // Получить все столы
        List<Table> tables = tableDAO.getAllTables();
        System.out.println("=== ВСЕ СТОЛЫ ===");
        for (Table t : tables) {
            System.out.println(t);
        }

        // Проверить открытые заказы
        System.out.println("\n=== ОТКРЫТЫЕ ЗАКАЗЫ ===");
        for (Table t : tables) {
            Order order = orderDAO.getOpenOrderByTableId(t.getId());
            if (order != null) {
                System.out.println("Стол " + t.getTableNumber() + " — заказ №" + order.getId());
            }
        }

        MainFrame.main(args);

    }
}