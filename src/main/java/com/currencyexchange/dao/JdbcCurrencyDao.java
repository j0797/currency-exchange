package com.currencyexchange.dao;

import com.currencyexchange.exception.AlreadyExistsException;
import com.currencyexchange.exception.DatabaseException;
import com.currencyexchange.model.Currency;
import com.currencyexchange.util.DatabaseConnection;
import com.currencyexchange.mapper.ResultSetMapper;

import java.sql.*;
import java.util.*;

public class JdbcCurrencyDao implements CurrencyDao {
    private static final String SQL_FIND_ALL = "SELECT id, code, full_name, sign FROM currencies";
    private static final String SQL_FIND_BY_CODE = "SELECT id, code, full_name, sign FROM currencies WHERE code = ?";
    private static final String SQL_FIND_BY_ID = "SELECT id, code, full_name, sign FROM currencies WHERE id = ?";
    private static final String SQL_INSERT = "INSERT INTO currencies (code, full_name, sign) VALUES (?, ?, ?)";

    public List<Currency> findAll() throws DatabaseException {
        List<Currency> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_FIND_ALL)) {
            while (rs.next()) {
                list.add(ResultSetMapper.mapCurrency(rs));
        }
        return list;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch all currencies", e);
        }
    }

    public Optional<Currency> findByCode(String code) throws DatabaseException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_FIND_BY_CODE)) {
            pstmt.setString(1, code.toUpperCase());
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? Optional.of(ResultSetMapper.mapCurrency(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch currency by code: " + code, e);
        }
    }

    public Optional<Currency> findById(Integer id) throws DatabaseException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_FIND_BY_ID)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                return (rs.next()) ? Optional.of(ResultSetMapper.mapCurrency(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch currency by id: " + id, e);
        }
    }

    public Currency save(Currency currency) throws DatabaseException, AlreadyExistsException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, currency.getCode().toUpperCase());
            pstmt.setString(2, currency.getName());
            pstmt.setString(3, currency.getSign());
            pstmt.executeUpdate();

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    currency.setId(rs.getInt(1));
                }
            }
            return currency;
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                throw new AlreadyExistsException("Currency with code " + currency.getCode() + " already exists");
            }
            throw new DatabaseException("Failed to save currency", e);
        }
    }
}
