package com.currencyexchange.servlet;

import com.currencyexchange.dto.response.ConversionResponseDto;
import com.currencyexchange.exception.DatabaseException;
import com.currencyexchange.exception.NotFoundException;
import com.currencyexchange.service.ConversionService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/exchange")
public class ExchangeServlet extends AbstractServlet {
    private final ConversionService conversionService = new ConversionService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String from = request.getParameter("from");
        String to = request.getParameter("to");
        String amountStr = request.getParameter("amount");

        if (from == null || to == null || amountStr == null) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Missing required parameters: from, to, amount");
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr);
        } catch (NumberFormatException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid amount format");
            return;
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Amount must be positive");
            return;
        }

        try {
            ConversionResponseDto result = conversionService.convert(from.toUpperCase(), to.toUpperCase(), amount);
            writeJson(response, result, HttpServletResponse.SC_OK);
        } catch (NotFoundException e) {
            sendError(response, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (DatabaseException e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        }
    }
}