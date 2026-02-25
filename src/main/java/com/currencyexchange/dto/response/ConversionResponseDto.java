package com.currencyexchange.dto.response;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class ConversionResponseDto {
    CurrencyResponseDto baseCurrency;
    CurrencyResponseDto targetCurrency;
    BigDecimal rate;
    BigDecimal amount;
    BigDecimal convertedAmount;
}