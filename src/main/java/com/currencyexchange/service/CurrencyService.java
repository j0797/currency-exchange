package com.currencyexchange.service;

import com.currencyexchange.dao.CurrencyDAO;
import com.currencyexchange.exception.DatabaseException;
import com.currencyexchange.exception.NotFoundException;
import com.currencyexchange.exception.ValidationException;
import com.currencyexchange.model.Currency;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
public class CurrencyService {
    private final CurrencyDAO currencyDAO = new CurrencyDAO();

    public List<Currency> findAllCurrencies() throws DatabaseException {
        log.info("Fetching all currencies");
        try {
            return currencyDAO.findAll();
        } catch (SQLException e) {
            log.error("Database error while fetching all currencies", e);
            throw new DatabaseException("Database error while fetching all currencies", e);
        }
    }

    public Currency findCurrencyByCode(String code) throws NotFoundException, DatabaseException {
        log.info("Finding currency by code: {}", code);
        try {
            return currencyDAO.findByCode(code)
                    .orElseThrow(() -> {
                        log.warn("Currency not found: {}", code);
                        return new NotFoundException("Currency not found: " + code);
                    });
        } catch (SQLException e) {
            log.error("Database error while fetching currency by code: {}", code, e);
            throw new DatabaseException("Database error while fetching currency by code", e);
        }
    }

    public Currency findCurrencyById(int id) throws NotFoundException, DatabaseException {
        log.info("Finding currency by id: {}", id);
        try {
            return currencyDAO.findById(id)
                    .orElseThrow(() -> new NotFoundException("Currency not found by id: " + id));
        } catch (SQLException e) {
            log.error("Database error while fetching currency by id: {}", id, e);
            throw new DatabaseException("Database error while fetching currency by id", e);
        }
    }

    public Currency createCurrency(Currency currency) throws DatabaseException, ValidationException {
        log.info("Creating currency: {}", currency.getCode());
        validateCurrency(currency);
        try {
            Optional<Currency> existing = currencyDAO.findByCode(currency.getCode());
            if (existing.isPresent()) {
                log.warn("Attempt to create duplicate currency: {}", currency.getCode());
                throw new ValidationException("Currency with code " + currency.getCode() + " already exists");
            }
            Currency saved = currencyDAO.save(currency);
            log.info("Currency created successfully: {} (id={})", saved.getCode(), saved.getId());
            return saved;
        } catch (SQLException e) {
            log.error("Database error while creating currency: {}", currency.getCode(), e);
            throw new DatabaseException("Database error while creating currency", e);
        }
    }

    private void validateCurrency(Currency currency) throws ValidationException {
        if (currency.getCode() == null || currency.getCode().length() != 3) {
            throw new ValidationException("Currency code must be exactly 3 characters");
        }
        if (currency.getName() == null || currency.getName().isBlank()) {
            throw new ValidationException("Currency full name is required");
        }
        if (currency.getSign() == null || currency.getSign().isBlank()) {
            throw new ValidationException("Currency sign is required");
        }
        if (currency.getSign().length() > 3) {
            throw new ValidationException("Currency sign is too long (max 5 characters)");
        }
    }
}