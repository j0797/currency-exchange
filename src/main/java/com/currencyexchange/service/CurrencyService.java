package com.currencyexchange.service;

import com.currencyexchange.dao.CurrencyDAO;
import com.currencyexchange.exception.DatabaseException;
import com.currencyexchange.exception.NotFoundException;
import com.currencyexchange.exception.ValidationException;
import com.currencyexchange.model.Currency;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CurrencyService {
    private final CurrencyDAO currencyDAO = new CurrencyDAO();

    public List<Currency> findAllCurrencies() throws DatabaseException {
        try {
            return currencyDAO.findAll();
        } catch (SQLException e) {
            throw new DatabaseException("Database error while fetching all currencies", e);
        }
    }

    public Currency findCurrencyByCode(String code) throws NotFoundException, DatabaseException {
        try {
            return currencyDAO.findByCode(code)
                    .orElseThrow(() -> new NotFoundException("Currency not found: " + code));
        } catch (SQLException e) {
            throw new DatabaseException("Database error while fetching currency by code", e);
        }
    }

    public Currency findCurrencyById(int id) throws NotFoundException, DatabaseException {
        try {
            return currencyDAO.findById(id)
                    .orElseThrow(() -> new NotFoundException("Currency not found by id: " + id));
        } catch (SQLException e) {
            throw new DatabaseException("Database error while fetching currency by id", e);
        }
    }

    public Currency createCurrency(Currency currency) throws DatabaseException, ValidationException {
        validateCurrency(currency);
        try {
        Optional<Currency> existing = currencyDAO.findByCode(currency.getCode());
        if (existing.isPresent()) {
            throw new ValidationException("Currency with code " + currency.getCode() + " already exists");
        }
        return currencyDAO.save(currency);
        } catch (SQLException e) {
            throw new DatabaseException("Database error while creating currency", e);
        }
    }

    private void validateCurrency(Currency currency) throws ValidationException {
        if (currency.getCode() == null || currency.getCode().length() != 3) {
            throw new ValidationException("Currency code must be exactly 3 characters");
        }
        if (currency.getFullName() == null || currency.getFullName().isBlank()) {
            throw new ValidationException("Currency full name is required");
        }
        if (currency.getSign() == null || currency.getSign().isBlank()) {
            throw new ValidationException("Currency sign is required");
        }
    }
}
