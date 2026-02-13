package com.currencyexchange.dao;

import com.currencyexchange.model.Currency;
import com.currencyexchange.util.DatabaseConnection;
import com.currencyexchange.util.ResultSetConverter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDAO {
    private static final String SQL_FIND_ALL = "SELECT id, code, full_name, sign FROM currencies";
    private static final String SQL_FIND_BY_CODE = "SELECT id, code, full_name, sign FROM currencies WHERE code = ?";
    private static final String SQL_FIND_BY_ID = "SELECT id, code, full_name, sign FROM currencies WHERE id = ?";
    private static final String SQL_INSERT = "INSERT INTO currencies (code, full_name, sign) VALUES (?, ?, ?)";

    public List<Currency> findAll() throws SQLException {
        List<Currency> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_FIND_ALL)) {
            while (rs.next()) {
                list.add(ResultSetConverter.mapCurrency(rs));
            }
        }
        return list;
    }

    public Optional<Currency> findByCode(String code) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_FIND_BY_CODE)) {
            pstmt.setString(1, code.toUpperCase());
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? Optional.of(ResultSetConverter.mapCurrency(rs)) : Optional.empty();
            }
        }
    }

    public Optional<Currency> findById(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_FIND_BY_ID)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                return (rs.next()) ? Optional.of(ResultSetConverter.mapCurrency(rs)) : Optional.empty();
            }
        }
    }

    public Currency save(Currency currency) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, currency.getCode().toUpperCase());
            pstmt.setString(2, currency.getFullName());
            pstmt.setString(3, currency.getSign());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    currency.setId(generatedKeys.getInt(1));
                }
            }
            return currency;
        }
    }
}
