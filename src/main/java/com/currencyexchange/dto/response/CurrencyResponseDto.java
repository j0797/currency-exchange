package com.currencyexchange.dto.response;

public class CurrencyResponseDto {
    private int id;
    private String code;
    private String fullName;
    private String sign;

    public CurrencyResponseDto(int id, String code, String fullName, String sign) {
        this.id = id;
        this.code = code;
        this.fullName = fullName;
        this.sign = sign;
    }

    public int getId() {
        return id;
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
