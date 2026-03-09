package com.currencyexchange.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Currency {
    private Integer id;
    private String code;
    private String name;
    private String sign;

    public Currency(String code, String name, String sign) {
        this.code = code;
        this.name = name;
        this.sign = sign;
    }
}
