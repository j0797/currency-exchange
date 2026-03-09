package com.currencyexchange.model;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ExchangeRate {
    private Integer id;
    private Currency baseCurrency;
    private Currency targetCurrency;
    private BigDecimal rate;

    public ExchangeRate(Currency baseCurrency, Currency targetCurrency, BigDecimal rate) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }
}
