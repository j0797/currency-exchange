package com.currencyexchange.service;

import com.currencyexchange.dao.CurrencyDao;
import com.currencyexchange.exception.AlreadyExistsException;
import com.currencyexchange.exception.DatabaseException;
import com.currencyexchange.exception.NotFoundException;
import com.currencyexchange.exception.ValidationException;
import com.currencyexchange.model.Currency;
import com.currencyexchange.util.Validator;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class CurrencyService {
    private final CurrencyDao currencyDao;

    public CurrencyService(CurrencyDao currencyDao) {
        this.currencyDao = currencyDao;
    }

    public List<Currency> findAllCurrencies() throws DatabaseException {
        log.info("Fetching all currencies");
        try {
            return currencyDao.findAll();
        } catch (DatabaseException e) {
            log.error("Database error while fetching all currencies", e);
            throw new DatabaseException("Database error while fetching all currencies", e);
        }
    }

    public Currency findCurrencyByCode(String code) throws NotFoundException, DatabaseException {
        log.info("Finding currency by code: {}", code);
        try {
            return currencyDao.findByCode(code)
                    .orElseThrow(() -> {
                        log.warn("Currency not found: {}", code);
                        return new NotFoundException("Currency not found: " + code);
                    });
        } catch (DatabaseException e) {
            log.error("Database error while fetching currency by code: {}", code, e);
            throw new DatabaseException("Database error while fetching currency by code", e);
        }
    }

    public Currency findCurrencyById(int id) throws NotFoundException, DatabaseException {
        log.info("Finding currency by id: {}", id);
        try {
            return currencyDao.findById(id)
                    .orElseThrow(() -> new NotFoundException("Currency not found by id: " + id));
        } catch (DatabaseException e) {
            log.error("Database error while fetching currency by id: {}", id, e);
            throw new DatabaseException("Database error while fetching currency by id", e);
        }
    }

    public Currency createCurrency(Currency currency) throws DatabaseException, ValidationException, AlreadyExistsException {
        validateCurrency(currency);
        try {
            Currency saved = currencyDao.save(currency);
            log.info("Currency created successfully: {} (id={})", saved.getCode(), saved.getId());
            return saved;
        } catch (AlreadyExistsException e) {
            log.warn("Attempt to create duplicate currency: {}", currency.getCode());
            throw e;
        } catch (DatabaseException e) {
            log.error("Database error while creating currency: {}", currency.getCode(), e);
            throw e;
        }
    }

    private void validateCurrency(Currency currency) throws ValidationException {
        Validator.validateCurrencyCode(currency.getCode());
        Validator.validateCurrencyName(currency.getName());
        Validator.validateCurrencySign(currency.getSign());
    }
}