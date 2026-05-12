package db;

import java.io.InputStream;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;

public class DBConnectionManager {

    private static String url;
    private static String user;
    private static String password;
    private static String activeDb;

    static {
        try {

            // ==================== ЗАГРУЗКА ОСНОВНОГО CONFIG ====================

            Properties mainProps = new Properties();

            try (InputStream input =
                         new FileInputStream("src/resources/config.properties")) {

                mainProps.load(input);

                activeDb = mainProps.getProperty("active", "postgresql");
            }

            // ==================== ЗАГРУЗКА CONFIG ДЛЯ ВЫБРАННОЙ БД ====================

            String configFile =
                    "src/resources/config-" + activeDb + ".properties";

            Properties dbProps = new Properties();

            try (InputStream input =
                         new FileInputStream(configFile)) {

                dbProps.load(input);

                url = dbProps.getProperty("db.url");
                user = dbProps.getProperty("db.user");
                password = dbProps.getProperty("db.password");

                Class.forName(dbProps.getProperty("db.driver"));
            }

            System.out.println("✅ Используется БД: " + activeDb);
            System.out.println("✅ URL: " + url);

            // ==================== СОЗДАНИЕ ТАБЛИЦ ====================

            if ("postgresql".equals(activeDb)) {
                createPostgresTables();
            } else if ("mysql".equals(activeDb)) {
                createMySQLTables();
            }

        } catch (Exception e) {

            System.err.println("❌ Ошибка загрузки БД:");
            e.printStackTrace();

            throw new RuntimeException(
                    "Ошибка загрузки конфигурации БД: " + e.getMessage()
            );
        }
    }

    // ==================== CONNECTION ====================

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(url, user, password);
    }

    public static String getActiveDb() {
        return activeDb;
    }

    // ==================== POSTGRESQL ====================

    private static void createPostgresTables() {

        String sql =
                "CREATE TABLE IF NOT EXISTS test_table (" +
                        "id SERIAL PRIMARY KEY, " +
                        "name VARCHAR(100))";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);

            System.out.println("✅ PostgreSQL таблицы созданы");

        } catch (Exception e) {

            System.err.println(
                    "❌ Ошибка PostgreSQL: " + e.getMessage()
            );
        }
    }

    // ==================== MYSQL ====================

    private static void createMySQLTables() {

        String sql =
                "CREATE TABLE IF NOT EXISTS test_table (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "name VARCHAR(100))";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);

            System.out.println("✅ MySQL таблицы созданы");

        } catch (Exception e) {

            System.err.println(
                    "❌ Ошибка MySQL: " + e.getMessage()
            );
        }
    }
}