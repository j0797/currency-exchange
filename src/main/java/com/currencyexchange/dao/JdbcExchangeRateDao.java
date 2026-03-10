package com.currencyexchange.dao;

import com.currencyexchange.exception.AlreadyExistsException;
import com.currencyexchange.exception.DatabaseException;
import com.currencyexchange.model.ExchangeRate;
import com.currencyexchange.util.DatabaseConnection;
import com.currencyexchange.mapper.ResultSetMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcExchangeRateDao implements ExchangeRateDao {


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
    private static final String SQL_UPDATE_BY_ID = "UPDATE exchange_rates SET rate = ? WHERE id = ?";

    public List<ExchangeRate> findAll() throws DatabaseException {
        List<ExchangeRate> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {
            while (rs.next()) {
                list.add(ResultSetMapper.mapExchangeRate(rs));
            }
        return list;
    } catch (SQLException e) {
        throw new DatabaseException("Failed to fetch all exchange rates", e);
    }
    }

    public Optional<ExchangeRate> findById(Integer id) throws DatabaseException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_FIND_BY_ID)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                return (rs.next())
                        ? Optional.of(ResultSetMapper.mapExchangeRate(rs))
                        : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch exchange rate by id: " + id, e);
        }
    }

    public Optional<ExchangeRate> findByPair(String baseCode, String targetCode) throws DatabaseException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_FIND_BY_PAIR)) {
            pstmt.setString(1, baseCode.toUpperCase());
            pstmt.setString(2, targetCode.toUpperCase());
            try (ResultSet rs = pstmt.executeQuery()) {
                return (rs.next())
                        ? Optional.of(ResultSetMapper.mapExchangeRate(rs))
                        : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch exchange rate by pair: " + baseCode + "-" + targetCode, e);
        }
    }

    public ExchangeRate save(ExchangeRate rate) throws DatabaseException, AlreadyExistsException {
        if (rate.getBaseCurrency().getId() == null || rate.getTargetCurrency().getId() == null) {
            throw new DatabaseException("Cannot save exchange rate: currency ID is missing", null);
        }
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, rate.getBaseCurrency().getId());
            pstmt.setInt(2, rate.getTargetCurrency().getId());
            pstmt.setBigDecimal(3, rate.getRate());
            pstmt.executeUpdate();

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    rate.setId(rs.getInt(1));
                }
            }
            return rate;
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                throw new AlreadyExistsException("Exchange rate for pair " + rate.getBaseCurrency().getCode() + "-" + rate.getTargetCurrency().getCode() + " already exists");
            }
            throw new DatabaseException("Failed to save exchange rate", e);
        }
    }

    public boolean update(ExchangeRate rate) throws DatabaseException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE_BY_ID)) {
            pstmt.setBigDecimal(1, rate.getRate());
            pstmt.setInt(2, rate.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update exchange rate", e);
        }
    }
}
