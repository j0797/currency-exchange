package com.currencyexchange.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class DatabaseConnection {
    private static final Properties props = new Properties();
    private static String DB_URL;
    static {
        loadProperties();
        loadDriver();
    }

    private static void loadProperties() {
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new RuntimeException("db.properties not found in classpath");
            }
            props.load(input);
            DB_URL = props.getProperty("db.url");
            log.info("Database URL loaded: {}", DB_URL);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load database configuration", e);
        }
    }

    private static void loadDriver() {
        try {
            String driver = props.getProperty("db.driver");
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite JDBC driver not found", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}