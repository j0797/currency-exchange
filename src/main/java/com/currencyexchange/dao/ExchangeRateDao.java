package com.currencyexchange.dao;

import com.currencyexchange.exception.DatabaseException;
import com.currencyexchange.model.ExchangeRate;

import java.util.Optional;

public interface ExchangeRateDao extends BaseDao<ExchangeRate, Integer> {
    Optional<ExchangeRate> findByPair(String baseCode, String targetCode) throws DatabaseException;

    boolean update(ExchangeRate rate) throws DatabaseException;
}