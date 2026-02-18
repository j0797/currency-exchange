package com.currencyexchange.service;

import com.currencyexchange.dto.response.ConversionResponseDto;
import com.currencyexchange.dto.response.CurrencyResponseDto;
import com.currencyexchange.exception.DatabaseException;
import com.currencyexchange.exception.NotFoundException;
import com.currencyexchange.model.Currency;
import com.currencyexchange.model.ExchangeRate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class ConversionService {
    private static final String USD_CODE = "USD";

    private final ExchangeRateService exchangeRateService;
    private final CurrencyService currencyService;

    public ConversionService() {
        this.exchangeRateService = new ExchangeRateService();
        this.currencyService = new CurrencyService();
    }

    public ConversionService(ExchangeRateService exchangeRateService, CurrencyService currencyService) {
        this.exchangeRateService = exchangeRateService;
        this.currencyService = currencyService;
    }

    public ConversionResponseDto convert(String fromCode, String toCode, BigDecimal amount)
            throws NotFoundException, DatabaseException {

        Currency from = currencyService.findCurrencyByCode(fromCode);
        Currency to = currencyService.findCurrencyByCode(toCode);

        Optional<ConversionResponseDto> direct = findDirect(from, to, amount);
        if (direct.isPresent()) {
            return direct.get();
        }

        Optional<ConversionResponseDto> reverse = findReverse(from, to, amount);
        if (reverse.isPresent()) {
            return reverse.get();
        }

        Optional<ConversionResponseDto> cross = findCross(from, to, amount);
        if (cross.isPresent()) {
            return cross.get();
        }

        throw new NotFoundException("Exchange rate not found for pair " + fromCode + "-" + toCode);
    }

    private Optional<ConversionResponseDto> findDirect(Currency from, Currency to, BigDecimal amount)
            throws DatabaseException {
        try {
            ExchangeRate rate = exchangeRateService.findExchangeRateByPair(from.getCode(), to.getCode());
            return Optional.of(buildConversionResponse(from, to, rate.getRate(), amount));
        } catch (NotFoundException e) {
            return Optional.empty();
        }
    }

    private Optional<ConversionResponseDto> findReverse(Currency from, Currency to, BigDecimal amount)
            throws DatabaseException {
        try {
            ExchangeRate rate = exchangeRateService.findExchangeRateByPair(to.getCode(), from.getCode());
            BigDecimal invertedRate = BigDecimal.ONE.divide(rate.getRate(), 6, RoundingMode.HALF_UP);
            return Optional.of(buildConversionResponse(to, from, invertedRate, amount));
        } catch (NotFoundException e) {
            return Optional.empty();
        }
    }

    private Optional<ConversionResponseDto> findCross(Currency from, Currency to, BigDecimal amount)
            throws DatabaseException {
        try {
            ExchangeRate usdToFrom = exchangeRateService.findExchangeRateByPair(USD_CODE, from.getCode());
            ExchangeRate usdToTo = exchangeRateService.findExchangeRateByPair(USD_CODE, to.getCode());

            BigDecimal crossRate = usdToTo.getRate().divide(usdToFrom.getRate(), 6, RoundingMode.HALF_UP);
            return Optional.of(buildConversionResponse(
                    usdToFrom.getTargetCurrency(),
                    usdToTo.getTargetCurrency(),
                    crossRate,
                    amount
            ));
        } catch (NotFoundException e) {
            return Optional.empty();
        }
    }

    private ConversionResponseDto buildConversionResponse(Currency from, Currency to, BigDecimal rate, BigDecimal amount) {
        CurrencyResponseDto fromDto = new CurrencyResponseDto(from.getCode(), from.getFullName(), from.getSign());
        CurrencyResponseDto toDto = new CurrencyResponseDto(to.getCode(), to.getFullName(), to.getSign());
        BigDecimal convertedAmount = amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
        return new ConversionResponseDto(fromDto, toDto, rate, amount, convertedAmount);
    }
}