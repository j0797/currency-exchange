package com.currencyexchange.service;

import com.currencyexchange.dao.ExchangeRateDAO;
import com.currencyexchange.exception.DatabaseException;
import com.currencyexchange.exception.NotFoundException;
import com.currencyexchange.exception.ValidationException;
import com.currencyexchange.model.Currency;
import com.currencyexchange.model.ExchangeRate;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
public class ExchangeRateService {
    private final ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAO();
    private final CurrencyService currencyService = new CurrencyService();

    public List<ExchangeRate> findAllExchangeRates() throws DatabaseException {
        log.info("Fetching all exchange rates");
        try {
            return exchangeRateDAO.findAll();
        } catch (SQLException e) {
            log.error("Database error while fetching all exchange rates", e);
            throw new DatabaseException("Database error while fetching all exchange rates", e);
        }
    }

    public ExchangeRate findExchangeRateById(int id) throws DatabaseException, NotFoundException {
        log.info("Finding currency by id: {}", id);
        try {
            return exchangeRateDAO.findById(id)
                    .orElseThrow(() -> new NotFoundException("Exchange rate not found with id: " + id));
        } catch (SQLException e) {
            log.error("Database error while fetching exchange rate by id: {}", id, e);
            throw new DatabaseException("Database error while fetching exchange rate by id", e);
        }
    }

    public ExchangeRate findExchangeRateByPair(String baseCode, String targetCode)
            throws DatabaseException, NotFoundException {
        log.info("Finding exchange rate for pair: {}-{}", baseCode, targetCode);
        try {
            return exchangeRateDAO.findByPair(baseCode, targetCode)
                    .orElseThrow(() -> new NotFoundException(
                            "Exchange rate not found for pair " + baseCode + "-" + targetCode));
        } catch (SQLException e) {
            log.error("Database error while fetching exchange rate by pair: {}-{}", baseCode, targetCode, e);
            throw new DatabaseException("Database error while fetching exchange rate by pair", e);
        }
    }

    public ExchangeRate createExchangeRate(String baseCode, String targetCode, BigDecimal rate)
            throws DatabaseException, NotFoundException, ValidationException {
        try {
            Currency base = currencyService.findCurrencyByCode(baseCode);
            Currency target = currencyService.findCurrencyByCode(targetCode);
            if (base.getId().equals(target.getId())) {
                log.warn("Attempt to create exchange rate with same base and target currencies: {}-{}", baseCode, targetCode);
                throw new ValidationException("Base and target currencies must be different");
            }
            if (rate == null || rate.compareTo(BigDecimal.ZERO) <= 0) {
                log.warn("Attempt to create exchange rate with non-positive rate: {}", rate);
                throw new ValidationException("Rate must be positive");
            }
            Optional<ExchangeRate> existing = exchangeRateDAO.findByPair(baseCode, targetCode);
            if (existing.isPresent()) {
                log.warn("Attempt to create duplicate exchange rate for pair {}-{}", baseCode, targetCode);
                throw new ValidationException("Exchange rate for pair " + baseCode + "-" + targetCode + " already exists");
            }
            ExchangeRate exchangeRate = new ExchangeRate(base, target, rate);
            return exchangeRateDAO.save(exchangeRate);
        } catch (SQLException e) {
            log.error("Database error while creating exchange rate for pair {}-{}", baseCode, targetCode, e);
            throw new DatabaseException("Database error while creating exchange rate", e);
        }
    }

    public void updateExchangeRate(String baseCode, String targetCode, BigDecimal newRate)
            throws DatabaseException, NotFoundException, ValidationException {
        try {
            ExchangeRate existing = findExchangeRateByPair(baseCode, targetCode);
            if (newRate == null || newRate.compareTo(BigDecimal.ZERO) <= 0) {
                log.warn("Attempt to update exchange rate with non-positive rate: {}", newRate);
                throw new ValidationException("Rate must be positive");
            }
            existing.setRate(newRate);
            exchangeRateDAO.update(existing);
        } catch (SQLException e) {
            log.error("Database error while updating exchange rate for pair {}-{}", baseCode, targetCode, e);
            throw new DatabaseException("Database error while updating exchange rate", e);
        }
    }
}
