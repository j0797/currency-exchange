package com.currencyexchange.dao;

import com.currencyexchange.model.Currency;

import java.sql.SQLException;
import java.util.Optional;

public interface CurrencyDao extends BaseDao<Currency, Integer> {
    Optional<Currency> findByCode(String code) throws SQLException;
}
