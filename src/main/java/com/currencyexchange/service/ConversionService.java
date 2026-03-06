package com.currencyexchange.service;

import com.currencyexchange.dto.response.ConversionResponseDto;
import com.currencyexchange.dto.response.CurrencyResponseDto;
import com.currencyexchange.exception.DatabaseException;
import com.currencyexchange.exception.NotFoundException;
import com.currencyexchange.model.Currency;
import com.currencyexchange.model.ExchangeRate;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Slf4j
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

        log.info("Конвертация {} из {} в {}", amount, fromCode, toCode);
        Currency from = currencyService.findCurrencyByCode(fromCode);
        Currency to = currencyService.findCurrencyByCode(toCode);

        Optional<ConversionResponseDto> direct = findDirect(from, to, amount);
        if (direct.isPresent()) {
            log.debug("Использован прямой курс {}-{}", fromCode, toCode);
            return direct.get();
        }

        Optional<ConversionResponseDto> reverse = findReverse(from, to, amount);
        if (reverse.isPresent()) {
            log.debug("Использован обратный курс {}-{}", fromCode, toCode);
            return reverse.get();
        }

        Optional<ConversionResponseDto> cross = findCross(from, to, amount);
        if (cross.isPresent()) {
            log.debug("Использован кросс-курс через USD для пары {}-{}", fromCode, toCode);
            return cross.get();
        }

        log.warn("Курс для пары {}-{} не найден ни одним способом", fromCode, toCode);
        throw new NotFoundException("Exchange rate not found for pair " + fromCode + "-" + toCode);
    }

    private Optional<ConversionResponseDto> findDirect(Currency from, Currency to, BigDecimal amount)
            throws DatabaseException {
        try {
            ExchangeRate rate = exchangeRateService.findExchangeRateByPair(from.getCode(), to.getCode());
            log.debug("Найден прямой курс {}-{}: {}", from.getCode(), to.getCode(), rate.getRate());
            return Optional.of(buildConversionResponse(from, to, rate.getRate(), amount));
        } catch (NotFoundException e) {
            log.debug("Прямой курс {}-{} не найден", from.getCode(), to.getCode());
            return Optional.empty();
        }
    }

    private Optional<ConversionResponseDto> findReverse(Currency from, Currency to, BigDecimal amount)
            throws DatabaseException {
        try {
            ExchangeRate rate = exchangeRateService.findExchangeRateByPair(to.getCode(), from.getCode());
            BigDecimal invertedRate = BigDecimal.ONE.divide(rate.getRate(), 6, RoundingMode.HALF_UP);
            log.debug("Найден обратный курс {}-{}, инвертирован в {}", to.getCode(), from.getCode(), invertedRate);
            return Optional.of(buildConversionResponse(to, from, invertedRate, amount));
        } catch (NotFoundException e) {
            log.debug("Обратный курс {}-{} не найден", to.getCode(), from.getCode());
            return Optional.empty();
        }
    }

    private Optional<ConversionResponseDto> findCross(Currency from, Currency to, BigDecimal amount)
            throws DatabaseException {
        try {
            ExchangeRate usdToFrom = exchangeRateService.findExchangeRateByPair(USD_CODE, from.getCode());
            ExchangeRate usdToTo = exchangeRateService.findExchangeRateByPair(USD_CODE, to.getCode());

            BigDecimal crossRate = usdToTo.getRate().divide(usdToFrom.getRate(), 6, RoundingMode.HALF_UP);
            log.debug("Кросс-курс через USD для {}-{}: {}", from.getCode(), to.getCode(), crossRate);
            return Optional.of(buildConversionResponse(
                    usdToFrom.getTargetCurrency(),
                    usdToTo.getTargetCurrency(),
                    crossRate,
                    amount
            ));
        } catch (NotFoundException e) {
            log.debug("Кросс-курс через USD для {}-{} не найден", from.getCode(), to.getCode());
            return Optional.empty();
        }
    }

    private ConversionResponseDto buildConversionResponse(Currency from, Currency to, BigDecimal rate, BigDecimal amount) {
        CurrencyResponseDto fromDto = new CurrencyResponseDto(from.getId(), from.getCode(), from.getName(), from.getSign());
        CurrencyResponseDto toDto = new CurrencyResponseDto(to.getId(), to.getCode(), to.getName(), to.getSign());
        BigDecimal convertedAmount = amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
        ConversionResponseDto response = new ConversionResponseDto(fromDto, toDto, rate, amount, convertedAmount);
        log.debug("Результат конвертации: {}", response);
        return response;
    }
}