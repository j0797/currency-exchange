package com.currencyexchange.dto.response;

public class CurrencyResponseDto {
    private String code;
    private String fullName;
    private String sign;

    public CurrencyResponseDto(String code, String fullName, String sign) {
        this.code = code;
        this.fullName = fullName;
        this.sign = sign;
    }

    public String getCode() {
        return code;
    }

    public String getFullName() {
        return fullName;
    }

    public String getSign() {
        return sign;
    }
}
