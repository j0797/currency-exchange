package com.currencyexchange.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:sqlite:data/database.db";

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(DB_URL);
        connection.createStatement().execute("PRAGMA foreign_keys = ON;");
        return connection;
    }
}
