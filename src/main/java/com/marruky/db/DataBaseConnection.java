package com.marruky.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DataBaseConnection {

    private static Connection connection;

    private DataBaseConnection() {
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Properties properties = new Properties();
                properties.load(DataBaseConnection.class.getResourceAsStream("/application.properties"));
                String url = properties.getProperty("db.url");
                String user = properties.getProperty("db.user");
                String password = properties.getProperty("db.password");
                connection = DriverManager.getConnection(url, user, password);
            }
            return connection;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
