package com.currencyexchange.mapper;

import com.currencyexchange.dto.response.CurrencyResponseDto;
import com.currencyexchange.dto.response.ExchangeRateResponseDto;
import com.currencyexchange.model.ExchangeRate;

public class ExchangeRateMapper {

    private ExchangeRateMapper() {
    }

    public static ExchangeRateResponseDto toDto(ExchangeRate rate) {
        CurrencyResponseDto base = new CurrencyResponseDto(
                rate.getBaseCurrency().getId(),
                rate.getBaseCurrency().getCode(),
                rate.getBaseCurrency().getName(),
                rate.getBaseCurrency().getSign()
        );
        CurrencyResponseDto target = new CurrencyResponseDto(
                rate.getTargetCurrency().getId(),
                rate.getTargetCurrency().getCode(),
                rate.getTargetCurrency().getName(),
                rate.getTargetCurrency().getSign()
        );
        return new ExchangeRateResponseDto(rate.getId(), base, target, rate.getRate());
    }
}