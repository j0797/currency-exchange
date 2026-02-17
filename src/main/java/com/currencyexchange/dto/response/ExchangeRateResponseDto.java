package com.currencyexchange.dto.response;

import java.math.BigDecimal;

public class ExchangeRateResponseDto {
    private CurrencyResponseDto baseCurrency;
    private CurrencyResponseDto targetCurrency;
    private BigDecimal rate;

    public ExchangeRateResponseDto(CurrencyResponseDto baseCurrency, CurrencyResponseDto targetCurrency, BigDecimal rate) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }

    public CurrencyResponseDto getBaseCurrency() {
        return baseCurrency;
    }

    public CurrencyResponseDto getTargetCurrency() {
        return targetCurrency;
    }

    public BigDecimal getRate() {
        return rate;
    }
}
