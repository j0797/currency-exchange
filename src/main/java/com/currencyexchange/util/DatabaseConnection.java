package com.currencyexchange.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class DatabaseConnection {
    private static HikariDataSource dataSource;

    static {
        loadDataSource();
    }

    private static void loadDataSource() {
        Properties props = new Properties();
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new RuntimeException("db.properties not found in classpath");
            }
            props.load(input);

            HikariConfig config = new HikariConfig();
            String driver = props.getProperty("db.driver");
            String url = props.getProperty("db.url");
            if (driver == null || url == null) {
                throw new RuntimeException("Missing db.driver or db.url in db.properties");
            }
            config.setDriverClassName(driver);
            config.setJdbcUrl(url);

            config.setMaximumPoolSize(getIntProperty(props, "pool.maxSize", 10));
            config.setMinimumIdle(getIntProperty(props, "pool.minIdle", 2));
            config.setConnectionTimeout(getLongProperty(props, "pool.connectionTimeout", 30000));
            config.setIdleTimeout(getLongProperty(props, "pool.idleTimeout", 600000));
            config.setMaxLifetime(getLongProperty(props, "pool.maxLifetime", 1800000));

            dataSource = new HikariDataSource(config);
            log.info("Database connection pool initialized with URL: {}", props.getProperty("db.url"));

        } catch (IOException e) {
            throw new RuntimeException("Failed to load database configuration", e);
        }
    }

    private static int getIntProperty(Properties props, String key, int defaultValue) {
        String value = props.getProperty(key);
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            log.warn("Invalid number for property {}, using default: {}", key, defaultValue);
            return defaultValue;
        }
    }

    private static long getLongProperty(Properties props, String key, long defaultValue) {
        String value = props.getProperty(key);
        if (value == null) return defaultValue;
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            log.warn("Invalid number for property {}, using default: {}", key, defaultValue);
            return defaultValue;
        }
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource is not initialized");
        }
        return dataSource.getConnection();
    }
}