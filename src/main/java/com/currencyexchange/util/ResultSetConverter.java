package com.currencyexchange.util;

import com.currencyexchange.model.Currency;
import com.currencyexchange.model.ExchangeRate;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultSetConverter {
    private ResultSetConverter() {
    }

    public static Currency mapCurrency(ResultSet rs) throws SQLException {
        Currency currency = new Currency();
        currency.setId(rs.getInt("id"));
        currency.setCode(rs.getString("code"));
        currency.setFullName(rs.getString("full_name"));
        currency.setSign(rs.getString("sign"));

        return currency;
    }

    public static ExchangeRate mapExchangeRate(ResultSet rs) throws SQLException {
        ExchangeRate rate = new ExchangeRate();
        rate.setId(rs.getInt("rate_id"));
        rate.setRate(rs.getBigDecimal("rate"));

        Currency base = new Currency(
                rs.getInt("base_id"),
                rs.getString("base_code"),
                rs.getString("base_full_name"),
                rs.getString("base_sign")
        );
        rate.setBaseCurrency(base);

        Currency target = new Currency(
                rs.getInt("target_id"),
                rs.getString("target_code"),
                rs.getString("target_full_name"),
                rs.getString("target_sign")
        );
        rate.setTargetCurrency(target);
        return rate;
    }
}
