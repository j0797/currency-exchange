package com.currencyexchange.dto.request;

import com.currencyexchange.exception.ValidationException;

import java.math.BigDecimal;

public record ExchangeRateRequestDto(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {

    public ExchangeRateRequestDto {
        if (baseCurrencyCode == null || baseCurrencyCode.trim().isEmpty()) {
            throw new ValidationException("Base currency code is required");
        }
        if (targetCurrencyCode == null || targetCurrencyCode.trim().isEmpty()) {
            throw new ValidationException("Target currency code is required");
        }
        if (rate == null) {
            throw new ValidationException("Rate is required");
        }
        if (rate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Rate must be positive");
        }
    }
}