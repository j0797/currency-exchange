package com.currencyexchange.mapper;

import com.currencyexchange.dto.response.CurrencyResponseDto;
import com.currencyexchange.dto.response.ExchangeRateResponseDto;
import com.currencyexchange.model.ExchangeRate;

public class ExchangeRateMapper {

    private ExchangeRateMapper() {
    }

    public static ExchangeRateResponseDto toDto(ExchangeRate rate) {
        CurrencyResponseDto base = new CurrencyResponseDto(
                rate.getBaseCurrency().getCode(),
                rate.getBaseCurrency().getFullName(),
                rate.getBaseCurrency().getSign()
        );
        CurrencyResponseDto target = new CurrencyResponseDto(
                rate.getTargetCurrency().getCode(),
                rate.getTargetCurrency().getFullName(),
                rate.getTargetCurrency().getSign()
        );
        return new ExchangeRateResponseDto(base, target, rate.getRate());
    }
}