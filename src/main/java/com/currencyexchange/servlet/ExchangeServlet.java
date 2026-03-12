package com.currencyexchange.servlet;

import com.currencyexchange.dto.response.ExchangeResponseDto;
import com.currencyexchange.exception.ValidationException;
import com.currencyexchange.service.ExchangeService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigDecimal;

@Slf4j
@WebServlet("/exchange")
public class ExchangeServlet extends AbstractServlet {
    private ExchangeService conversionService;

    @Override
    public void init() throws ServletException {
        super.init();
        conversionService = (ExchangeService) getServletContext().getAttribute("exchangeService");
        if (conversionService == null) {
            throw new IllegalStateException("exchangeService not initialized in servlet context");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String from = request.getParameter("from");
        String to = request.getParameter("to");
        String amountStr = request.getParameter("amount");

        if (from == null || to == null || amountStr == null) {
            throw new ValidationException("Missing required parameters: from, to, amount");
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr);
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid amount format");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Amount must be positive");
        }

        log.debug("Processing conversion: {} {} -> {}", amount, from, to);

        ExchangeResponseDto result = conversionService.convert(from.toUpperCase(), to.toUpperCase(), amount);
        log.info("Conversion successful: {} {} -> {} {}", amount, from, result.convertedAmount(), to);
        writeJson(response, result, HttpServletResponse.SC_OK);
    }
}