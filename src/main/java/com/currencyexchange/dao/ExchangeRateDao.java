package com.currencyexchange.dao;

import com.currencyexchange.model.ExchangeRate;

import java.sql.SQLException;
import java.util.Optional;

public interface ExchangeRateDao extends BaseDao<ExchangeRate, Integer> {
    Optional<ExchangeRate> findByPair(String baseCode, String targetCode) throws SQLException;

    boolean update(ExchangeRate rate) throws SQLException;
}
