package com.currencyexchange.mapper;

import com.currencyexchange.dto.response.CurrencyResponseDto;
import com.currencyexchange.model.Currency;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CurrencyMapper {
    public static CurrencyResponseDto toDto(Currency currency) {
        return new CurrencyResponseDto(currency.getId(), currency.getCode(), currency.getName(), currency.getSign());
    }
}