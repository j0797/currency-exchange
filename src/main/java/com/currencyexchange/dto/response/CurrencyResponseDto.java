package com.currencyexchange.dto.response;

import lombok.Value;

@Value
public class CurrencyResponseDto {
    int id;
    String code;
    String fullName;
    String sign;
}
