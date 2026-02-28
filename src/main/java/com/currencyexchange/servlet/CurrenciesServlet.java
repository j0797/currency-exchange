package com.currencyexchange.servlet;

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
            String code = req.getParameter("code");
            String fullName = req.getParameter("name");
            String sign = req.getParameter("sign");

            if (code == null || fullName == null || sign == null) {
                throw new ValidationException("A required field is missing");
            }
            if (code.trim().isEmpty() || fullName.trim().isEmpty() || sign.trim().isEmpty()) {
                throw new ValidationException("Fields cannot be empty or contain only spaces");
            }

            Currency currency = new Currency(null, code, fullName, sign);
            Currency created = currencyService.createCurrency(currency);
            CurrencyResponseDto responseDto = CurrencyMapper.toDto(created);
            writeJson(resp, responseDto, HttpServletResponse.SC_CREATED);
        } catch (ValidationException e) {
            if (e.getMessage().contains("already exists")) {
                sendError(resp, HttpServletResponse.SC_CONFLICT, e.getMessage());
            } else {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            }
        } catch (DatabaseException e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        }
    }
}