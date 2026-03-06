package com.currencyexchange.servlet;

import com.currencyexchange.dto.response.CurrencyResponseDto;
import com.currencyexchange.exception.DatabaseException;
import com.currencyexchange.exception.NotFoundException;
import com.currencyexchange.exception.ValidationException;
import com.currencyexchange.mapper.CurrencyMapper;
import com.currencyexchange.model.Currency;
import com.currencyexchange.service.CurrencyService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@WebServlet("/currency/*")
public class CurrencyServlet extends AbstractServlet {
    private final CurrencyService currencyService = new CurrencyService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String code = null;
        try {
            code = parseCurrencyCode(req.getPathInfo());
            Currency currency = currencyService.findCurrencyByCode(code);
            CurrencyResponseDto dto = CurrencyMapper.toDto(currency);
            writeJson(resp, dto, HttpServletResponse.SC_OK);
        } catch (ValidationException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (NotFoundException e) {
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (DatabaseException e) {
            log.error("Database error in GET /currency/* for code: {}", code, e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        }
    }

    private String parseCurrencyCode(String pathInfo) throws ValidationException {
        if (pathInfo == null || pathInfo.length() != 4) {
            throw new ValidationException("Invalid currency code format. Expected e.g. /currency/USDEUR");
        }
        String code = pathInfo.substring(1);
        if (code.length() != 3) {
            throw new ValidationException("Currency code must be 3 characters");
        }
        if (!code.matches("[A-Z]{3}")) {
            throw new ValidationException("Currency code must be three uppercase letters");
        }
        return code;
    }
}