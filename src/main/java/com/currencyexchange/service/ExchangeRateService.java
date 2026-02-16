package com.currencyexchange.service;

import com.currencyexchange.dao.ExchangeRateDAO;
import com.currencyexchange.exception.NotFoundException;
import com.currencyexchange.exception.ValidationException;
import com.currencyexchange.model.Currency;
import com.currencyexchange.model.ExchangeRate;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ExchangeRateService {
    private final ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAO();
    private final CurrencyService currencyService = new CurrencyService();

    public List<ExchangeRate> findAllExchangeRates() throws SQLException {
        return exchangeRateDAO.findAll();
    }

    public ExchangeRate findExchangeRateById(int id) throws SQLException, NotFoundException {
        return exchangeRateDAO.findById(id)
                .orElseThrow(() -> new NotFoundException("Exchange rate not found with id: " + id));
    }

    public ExchangeRate findExchangeRateByPair(String baseCode, String targetCode)
            throws SQLException, NotFoundException {
        return exchangeRateDAO.findByPair(baseCode, targetCode)
                .orElseThrow(() -> new NotFoundException(
                        "Exchange rate not found for pair " + baseCode + "-" + targetCode));
    }

    public ExchangeRate createExchangeRate(String baseCode, String targetCode, BigDecimal rate)
            throws SQLException, NotFoundException, ValidationException {
        Currency base = currencyService.findCurrencyByCode(baseCode);
        Currency target = currencyService.findCurrencyByCode(targetCode);
        if (base.getId().equals(target.getId())) {
            throw new ValidationException("Base and target currencies must be different");
        }
        if (rate == null || rate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Rate must be positive");
        }
        Optional<ExchangeRate> existing = exchangeRateDAO.findByPair(baseCode, targetCode);
        if (existing.isPresent()) {
            throw new ValidationException("Exchange rate for pair " + baseCode + "-" + targetCode + " already exists");
        }
        ExchangeRate exchangeRate = new ExchangeRate(base, target, rate);
        return exchangeRateDAO.save(exchangeRate);
    }


    public void updateExchangeRate(String baseCode, String targetCode, BigDecimal newRate)
            throws SQLException, NotFoundException, ValidationException {
        ExchangeRate existing = findExchangeRateByPair(baseCode, targetCode);
        if (newRate == null || newRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Rate must be positive");
        }
        existing.setRate(newRate);
        exchangeRateDAO.update(existing);
    }
}
