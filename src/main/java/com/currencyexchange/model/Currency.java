package com.currencyexchange.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Currency {
    private Integer id;
    private String code;
    private String name;
    private String sign;
}
