package com.currencyexchange.dto.response;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class ExchangeRateResponseDto {
    int id;
    CurrencyResponseDto baseCurrency;
    CurrencyResponseDto targetCurrency;
    BigDecimal rate;
}
