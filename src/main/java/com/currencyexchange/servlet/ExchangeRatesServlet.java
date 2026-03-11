package com.currencyexchange.servlet;

import com.currencyexchange.dto.request.ExchangeRateRequestDto;
import com.currencyexchange.dto.response.ExchangeRateResponseDto;
import com.currencyexchange.exception.DatabaseException;
import com.currencyexchange.exception.NotFoundException;
import com.currencyexchange.exception.ValidationException;
import com.currencyexchange.mapper.ExchangeRateMapper;
import com.currencyexchange.model.ExchangeRate;
import com.currencyexchange.service.ExchangeRateService;
import com.currencyexchange.exception.AlreadyExistsException;
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
        try {
            String baseCurrencyCode = req.getParameter("baseCurrencyCode");
            String targetCurrencyCode = req.getParameter("targetCurrencyCode");
            String rateParam = req.getParameter("rate");

            if (baseCurrencyCode == null || targetCurrencyCode == null || rateParam == null) {
                throw new ValidationException("Missing required fields");
            }

            baseCurrencyCode = baseCurrencyCode.trim();
            targetCurrencyCode = targetCurrencyCode.trim();

            BigDecimal rate;
            try {
                rate = new BigDecimal(rateParam);
            } catch (NumberFormatException e) {
                throw new ValidationException("Invalid rate format");
            }

            ExchangeRateRequestDto requestDto = new ExchangeRateRequestDto(baseCurrencyCode, targetCurrencyCode, rate);

            ExchangeRate created = exchangeRateService.createExchangeRate(
                    requestDto.baseCurrencyCode(),
                    requestDto.targetCurrencyCode(),
                    requestDto.rate()
            );
            ExchangeRateResponseDto responseDto = ExchangeRateMapper.toDto(created);
            writeJson(resp, responseDto, HttpServletResponse.SC_CREATED);
        } catch (AlreadyExistsException e) {
                sendError(resp, HttpServletResponse.SC_CONFLICT, e.getMessage());
        } catch (ValidationException e) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (NotFoundException e) {
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (DatabaseException e) {
            log.error("Database error in POST /exchangeRates", e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        }
    }
}