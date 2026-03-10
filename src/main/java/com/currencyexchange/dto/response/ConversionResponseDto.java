package com.currencyexchange.dto.response;

import java.math.BigDecimal;

public record ConversionResponseDto(CurrencyResponseDto baseCurrency, CurrencyResponseDto targetCurrency,
                                    BigDecimal rate, BigDecimal amount, BigDecimal convertedAmount) {
}