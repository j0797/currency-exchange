package com.currencyexchange.mapper;

import com.currencyexchange.dto.response.CurrencyResponseDto;
import com.currencyexchange.model.Currency;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CurrencyMapper {
    CurrencyMapper INSTANCE = Mappers.getMapper(CurrencyMapper.class);

    CurrencyResponseDto toDto(Currency currency);
}