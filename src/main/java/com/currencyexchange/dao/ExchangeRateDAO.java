package com.currencyexchange.dao;

import com.currencyexchange.model.ExchangeRate;
import com.currencyexchange.util.DatabaseConnection;
import com.currencyexchange.util.ResultSetConverter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDAO {


    private static final String SQL_SELECT_ALL = """
            SELECT
                er.id         AS rate_id,
                er.rate       AS rate,
                bc.id         AS base_id,
                bc.code       AS base_code,
                bc.full_name  AS base_full_name,
                bc.sign       AS base_sign,
                tc.id         AS target_id,
                tc.code       AS target_code,
                tc.full_name  AS target_full_name,
                tc.sign       AS target_sign
            FROM exchange_rates er
            JOIN currencies bc ON er.base_currency_id = bc.id
            JOIN currencies tc ON er.target_currency_id = tc.id
            """;

    private static final String SQL_FIND_BY_ID = SQL_SELECT_ALL + " WHERE er.id = ?";
    private static final String SQL_FIND_BY_PAIR = SQL_SELECT_ALL + " WHERE bc.code = ? AND tc.code = ?";
    private static final String SQL_INSERT =
            "INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate) VALUES (?, ?, ?)";

    public List<ExchangeRate> findAll() throws SQLException {
        List<ExchangeRate> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {
            while (rs.next()) {
                list.add(ResultSetConverter.mapExchangeRate(rs));
            }
        }
        return list;
    }

    public Optional<ExchangeRate> findById(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_FIND_BY_ID)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                return (rs.next())
                        ? Optional.of(ResultSetConverter.mapExchangeRate(rs))
                        : Optional.empty();
            }
        }
    }

    public Optional<ExchangeRate> findByPair(String baseCode, String targetCode) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_FIND_BY_PAIR)) {
            pstmt.setString(1, baseCode.toUpperCase());
            pstmt.setString(2, targetCode.toUpperCase());
            try (ResultSet rs = pstmt.executeQuery()) {
                return (rs.next())
                        ? Optional.of(ResultSetConverter.mapExchangeRate(rs))
                        : Optional.empty();
            }
        }
    }

    public ExchangeRate save(ExchangeRate rate) throws SQLException {
        if (rate.getBaseCurrency().getId() == null || rate.getTargetCurrency().getId() == null) {
            throw new SQLException("Cannot save exchange rate: currency ID is missing");
        }
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, rate.getBaseCurrency().getId());
            pstmt.setInt(2, rate.getTargetCurrency().getId());
            pstmt.setBigDecimal(3, rate.getRate());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    rate.setId(generatedKeys.getInt(1));
                }
            }
            return rate;
        }
    }
}
