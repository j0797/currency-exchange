package com.currencyexchange.util;

import com.currencyexchange.exception.ValidationException;

import java.math.BigDecimal;

public class Validator {
    private static void requireNonNullOrBlank(String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " is required");
        }
    }

    public static void validateCurrencyCode(String code) throws ValidationException {
        requireNonNullOrBlank(code, "Currency code");
        String trimmed = code.trim();
        if (trimmed.length() != 3) {
            throw new ValidationException("Currency code must be exactly 3 characters");
        }
        if (!trimmed.matches("[A-Z]{3}")) {
            throw new ValidationException("Currency code must be three uppercase letters");
        }
    }

    public static void validateCurrencyName(String name) throws ValidationException {
        requireNonNullOrBlank(name, "Currency full name");
    }

    public static void validateCurrencySign(String sign) throws ValidationException {
        requireNonNullOrBlank(sign, "Currency sign");
        String trimmed = sign.trim();
        if (trimmed.length() > 3) {
            throw new ValidationException("Currency sign is too long (max 3 characters)");
        }
    }

    public static void validateCurrencyAll(String code, String name, String sign) throws ValidationException {
        validateCurrencyCode(code);
        validateCurrencyName(name);
        validateCurrencySign(sign);
    }

    public static void validateRate(BigDecimal rate) throws ValidationException {
        if (rate == null) {
            throw new ValidationException("Rate is required");
        }
        if (rate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Rate must be positive");
        }
    }

    public static void validateCurrencyPair(String baseCode, String targetCode) throws ValidationException {
        validateCurrencyCode(baseCode);
        validateCurrencyCode(targetCode);
        if (baseCode.trim().equalsIgnoreCase(targetCode.trim())) {
            throw new ValidationException("Base and target currencies must be different");
        }
    }
}
