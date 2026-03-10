package com.currencyexchange.mapper;

import com.currencyexchange.dto.response.CurrencyResponseDto;
import com.currencyexchange.dto.response.ExchangeRateResponseDto;
import com.currencyexchange.model.ExchangeRate;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ExchangeRateMapper {

    public static ExchangeRateResponseDto toDto(ExchangeRate rate) {
        CurrencyResponseDto base = CurrencyMapper.toDto(rate.getBaseCurrency());
        CurrencyResponseDto target = CurrencyMapper.toDto(rate.getTargetCurrency());
        return new ExchangeRateResponseDto(rate.getId(), base, target, rate.getRate());
    }
}