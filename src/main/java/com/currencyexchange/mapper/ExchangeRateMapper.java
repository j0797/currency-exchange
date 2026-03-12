package com.currencyexchange.mapper;

import com.currencyexchange.dto.response.ExchangeRateResponseDto;
import com.currencyexchange.model.ExchangeRate;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = CurrencyMapper.class)
public interface ExchangeRateMapper {
    ExchangeRateMapper INSTANCE = Mappers.getMapper(ExchangeRateMapper.class);

    ExchangeRateResponseDto toDto(ExchangeRate rate);
}