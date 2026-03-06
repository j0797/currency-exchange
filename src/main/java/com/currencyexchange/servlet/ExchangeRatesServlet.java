package com.currencyexchange.servlet;

import com.currencyexchange.dto.response.ExchangeRateResponseDto;
import com.currencyexchange.exception.DatabaseException;
import com.currencyexchange.exception.NotFoundException;
import com.currencyexchange.exception.ValidationException;
import com.currencyexchange.mapper.ExchangeRateMapper;
import com.currencyexchange.model.ExchangeRate;
import com.currencyexchange.service.ExchangeRateService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends AbstractServlet {
    private final ExchangeRateService exchangeRateService = new ExchangeRateService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            List<ExchangeRate> rates = exchangeRateService.findAllExchangeRates();
            log.debug("Returning {} exchange rates", rates.size());
            List<ExchangeRateResponseDto> responseList = rates.stream()
                    .map(ExchangeRateMapper::toDto)
                    .collect(Collectors.toList());

            writeJson(resp, responseList, HttpServletResponse.SC_OK);
        } catch (DatabaseException e) {
            log.error("Database error while fetching all exchange rates", e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = null;
        String targetCurrencyCode = null;
        try {
            baseCurrencyCode = req.getParameter("baseCurrencyCode");
            targetCurrencyCode = req.getParameter("targetCurrencyCode");
            String rateParam = req.getParameter("rate");

            if (baseCurrencyCode == null || targetCurrencyCode == null || rateParam == null) {
                throw new ValidationException("Missing required fields");
            }
            if (baseCurrencyCode.trim().isEmpty() || targetCurrencyCode.trim().isEmpty()) {
                throw new ValidationException("Currency codes cannot be empty or contain only spaces");
            }

            BigDecimal rate;
            try {
                rate = new BigDecimal(rateParam);
            } catch (NumberFormatException e) {
                throw new ValidationException("Invalid rate format");
            }

            ExchangeRate created = exchangeRateService.createExchangeRate(baseCurrencyCode, targetCurrencyCode, rate);
            log.info("Exchange rate created for {}-{} with rate {}", baseCurrencyCode, targetCurrencyCode, rate);
            ExchangeRateResponseDto responseDto = ExchangeRateMapper.toDto(created);
            writeJson(resp, responseDto, HttpServletResponse.SC_CREATED);
        } catch (ValidationException e) {
            if (e.getMessage().contains("already exists")) {
                sendError(resp, HttpServletResponse.SC_CONFLICT, e.getMessage());
            } else {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            }
        } catch (NotFoundException e) {
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (DatabaseException e) {
            log.error("Database error while creating exchange rate for {}-{}", baseCurrencyCode, targetCurrencyCode, e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        }
    }
}