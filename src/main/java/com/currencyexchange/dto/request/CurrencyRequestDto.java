package com.currencyexchange.dto.request;

import com.currencyexchange.exception.ValidationException;

public record CurrencyRequestDto(String code, String name, String sign) {

    public CurrencyRequestDto {
        if (code == null || code.trim().isEmpty()) {
            throw new ValidationException("Currency code is required");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Currency name is required");
        }
        if (sign == null || sign.trim().isEmpty()) {
            throw new ValidationException("Currency sign is required");
        }
        if (code.trim().length() != 3) {
            throw new ValidationException("Currency code must be exactly 3 characters");
        }
    }
}