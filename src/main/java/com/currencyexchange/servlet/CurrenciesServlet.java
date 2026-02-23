package com.currencyexchange.servlet;

import com.currencyexchange.dto.request.CurrencyRequestDto;
import com.currencyexchange.dto.response.CurrencyResponseDto;
import com.currencyexchange.exception.DatabaseException;
import com.currencyexchange.exception.ValidationException;
import com.currencyexchange.mapper.CurrencyMapper;
import com.currencyexchange.model.Currency;
import com.currencyexchange.service.CurrencyService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/currencies")
public class CurrenciesServlet extends AbstractServlet {
    private final CurrencyService currencyService = new CurrencyService();

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            List<Currency> currencies = currencyService.findAllCurrencies();
            List<CurrencyResponseDto> responseList = currencies.stream()
                    .map(CurrencyMapper::toDto)
                    .collect(Collectors.toList());

            writeJson(resp, responseList, HttpServletResponse.SC_OK);
        } catch (DatabaseException e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            CurrencyRequestDto requestDto = gson.fromJson(req.getReader(), CurrencyRequestDto.class);

            Currency currency = new Currency();
            currency.setCode(requestDto.getCode());
            currency.setFullName(requestDto.getFullName());
            currency.setSign(requestDto.getSign());

            Currency created = currencyService.createCurrency(currency);
            CurrencyResponseDto responseDto = CurrencyMapper.toDto(created);

            writeJson(resp, responseDto, HttpServletResponse.SC_CREATED);
        } catch (ValidationException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (DatabaseException e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        }
    }
}