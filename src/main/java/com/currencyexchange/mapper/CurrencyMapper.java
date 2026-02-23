package com.currencyexchange.mapper;

import com.currencyexchange.dto.response.CurrencyResponseDto;
import com.currencyexchange.model.Currency;

public class CurrencyMapper {
    private CurrencyMapper() {
    }

    public static CurrencyResponseDto toDto(Currency currency) {
        return new CurrencyResponseDto(currency.getCode(), currency.getFullName(), currency.getSign());
    }
}