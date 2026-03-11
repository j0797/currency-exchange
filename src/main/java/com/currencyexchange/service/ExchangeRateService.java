package com.currencyexchange.service;

import com.currencyexchange.dao.ExchangeRateDao;
import com.currencyexchange.dao.JdbcExchangeRateDao;
import com.currencyexchange.exception.AlreadyExistsException;
import com.currencyexchange.exception.DatabaseException;
import com.currencyexchange.exception.NotFoundException;
import com.currencyexchange.exception.ValidationException;
import com.currencyexchange.model.Currency;
import com.currencyexchange.model.ExchangeRate;
import com.currencyexchange.util.Validator;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
public class ExchangeRateService {
    private final ExchangeRateDao exchangeRateDao = new JdbcExchangeRateDao();
    private final CurrencyService currencyService = new CurrencyService();

    public List<ExchangeRate> findAllExchangeRates() throws DatabaseException {
        log.info("Fetching all exchange rates");
        try {
            return exchangeRateDao.findAll();
        } catch (DatabaseException e) {
            log.error("Database error while fetching all exchange rates", e);
            throw new DatabaseException("Database error while fetching all exchange rates", e);
        }
    }

    public ExchangeRate findExchangeRateById(int id) throws DatabaseException, NotFoundException {
        log.info("Finding exchange rate by id: {}", id);
        try {
            return exchangeRateDao.findById(id)
                    .orElseThrow(() -> new NotFoundException("Exchange rate not found with id: " + id));
        } catch (DatabaseException e) {
            log.error("Database error while fetching exchange rate by id: {}", id, e);
            throw new DatabaseException("Database error while fetching exchange rate by id", e);
        }
    }

    public ExchangeRate findExchangeRateByPair(String baseCode, String targetCode)
            throws DatabaseException, NotFoundException {
        log.info("Finding exchange rate for pair: {}-{}", baseCode, targetCode);
        try {
            return exchangeRateDao.findByPair(baseCode, targetCode)
                    .orElseThrow(() -> new NotFoundException(
                            "Exchange rate not found for pair " + baseCode + "-" + targetCode));
        } catch (DatabaseException e) {
            log.error("Database error while fetching exchange rate by pair: {}-{}", baseCode, targetCode, e);
            throw new DatabaseException("Database error while fetching exchange rate by pair", e);
        }
    }

    public ExchangeRate createExchangeRate(String baseCode, String targetCode, BigDecimal rate)
            throws DatabaseException, NotFoundException, ValidationException, AlreadyExistsException {

        Validator.validateCurrencyPair(baseCode, targetCode);
        Validator.validateRate(rate);

        try {
            Currency base = currencyService.findCurrencyByCode(baseCode);
            Currency target = currencyService.findCurrencyByCode(targetCode);
            ExchangeRate exchangeRate = new ExchangeRate(base, target, rate);
            return exchangeRateDao.save(exchangeRate);
        } catch (AlreadyExistsException e) {
                log.warn("Attempt to create duplicate exchange rate for pair {}-{}", baseCode, targetCode);
                throw e;
        } catch (DatabaseException e) {
            log.error("Database error while creating exchange rate for pair {}-{}", baseCode, targetCode, e);
            throw new DatabaseException("Database error while creating exchange rate", e);
        }
    }

    public void updateExchangeRate(String baseCode, String targetCode, BigDecimal newRate)
            throws DatabaseException, NotFoundException, ValidationException {

        Validator.validateRate(newRate);

        try {
            ExchangeRate existing = findExchangeRateByPair(baseCode, targetCode);
            existing.setRate(newRate);
            exchangeRateDao.update(existing);
        } catch (DatabaseException e) {
            log.error("Database error while updating exchange rate for pair {}-{}", baseCode, targetCode, e);
            throw new DatabaseException("Database error while updating exchange rate", e);
        }
    }
}
