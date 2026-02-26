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

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends AbstractServlet {
    private final ExchangeRateService exchangeRateService = new ExchangeRateService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String[] codes = parseCurrencyPair(req.getPathInfo());
            String baseCode = codes[0];
            String targetCode = codes[1];

            ExchangeRate rate = exchangeRateService.findExchangeRateByPair(baseCode, targetCode);
            ExchangeRateResponseDto dto = ExchangeRateMapper.toDto(rate);
            writeJson(resp, dto, HttpServletResponse.SC_OK);
        } catch (ValidationException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (NotFoundException e) {
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (DatabaseException e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        }
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String[] codes = parseCurrencyPair(req.getPathInfo());
            String baseCode = codes[0];
            String targetCode = codes[1];

            StringBuilder body = new StringBuilder();
            String line;
            try (BufferedReader reader = req.getReader()) {
                while ((line = reader.readLine()) != null) {
                    body.append(line);
                }
            }
            String bodyStr = body.toString();

            String rateParam = null;
            String[] pairs = bodyStr.split("&");
            for (String pair : pairs) {
                String[] kv = pair.split("=");
                if (kv.length == 2 && "rate".equals(kv[0])) {
                    rateParam = URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
                    break;
                }
            }

            if (rateParam == null) {
                throw new ValidationException("Missing 'rate' field");
            }
            BigDecimal newRate;
            try {
                newRate = new BigDecimal(rateParam);
            } catch (NumberFormatException e) {
                throw new ValidationException("Invalid rate format");
            }

            exchangeRateService.updateExchangeRate(baseCode, targetCode, newRate);
            ExchangeRate updated = exchangeRateService.findExchangeRateByPair(baseCode, targetCode);
            ExchangeRateResponseDto dto = ExchangeRateMapper.toDto(updated);
            writeJson(resp, dto, HttpServletResponse.SC_OK);
        } catch (NotFoundException e) {
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (ValidationException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (DatabaseException e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        }
    }

    private String[] parseCurrencyPair(String pathInfo) throws ValidationException {
        if (pathInfo == null || pathInfo.length() != 7) {
            throw new ValidationException("Invalid currency pair format. Expected e.g. /exchangeRate/USDEUR");
        }
        String pair = pathInfo.substring(1);
        String baseCode = pair.substring(0, 3);
        String targetCode = pair.substring(3);
        return new String[]{baseCode, targetCode};
    }
}