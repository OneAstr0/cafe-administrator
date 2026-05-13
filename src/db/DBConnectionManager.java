package db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBConnectionManager {

    private static String url;
    private static String user;
    private static String password;
    private static String driver;

    static {
        try (InputStream input =
                     DBConnectionManager.class
                             .getClassLoader()
                             .getResourceAsStream("config.properties")) {

            Properties props = new Properties();
            props.load(input);

            // postgres или mysql
            String dbType = props.getProperty("db.type");

            driver = props.getProperty(dbType + ".driver");
            url = props.getProperty(dbType + ".url");
            user = props.getProperty(dbType + ".user");
            password = props.getProperty(dbType + ".password");

            Class.forName(driver);

            System.out.println("Используется БД: " + dbType);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка загрузки config.properties");
        }
    }

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(url, user, password);
    }
}