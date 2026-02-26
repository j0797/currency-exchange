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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends AbstractServlet {
    private final ExchangeRateService exchangeRateService = new ExchangeRateService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            List<ExchangeRate> rates = exchangeRateService.findAllExchangeRates();
            List<ExchangeRateResponseDto> responseList = rates.stream()
                    .map(ExchangeRateMapper::toDto)
                    .collect(Collectors.toList());

            writeJson(resp, responseList, HttpServletResponse.SC_OK);
        } catch (DatabaseException e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String baseCurrencyCode = req.getParameter("baseCurrencyCode");
            String targetCurrencyCode = req.getParameter("targetCurrencyCode");
            String rateParam = req.getParameter("rate");

            if (baseCurrencyCode == null || targetCurrencyCode == null || rateParam == null) {
                throw new ValidationException("Missing required fields");
            }
            BigDecimal rate;
            try {
                rate = new BigDecimal(rateParam);
            } catch (NumberFormatException e) {
                throw new ValidationException("Invalid rate format");
            }

            ExchangeRate created = exchangeRateService.createExchangeRate(baseCurrencyCode, targetCurrencyCode, rate);
            ExchangeRateResponseDto responseDto = ExchangeRateMapper.toDto(created);
            writeJson(resp, responseDto, HttpServletResponse.SC_CREATED);
        } catch (ValidationException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (NotFoundException e) {
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (DatabaseException e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        }
    }
}