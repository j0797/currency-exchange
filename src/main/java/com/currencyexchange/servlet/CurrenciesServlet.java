package com.currencyexchange.servlet;

import com.currencyexchange.dto.request.CurrencyRequestDto;
import com.currencyexchange.dto.response.CurrencyResponseDto;
import com.currencyexchange.mapper.CurrencyMapper;
import com.currencyexchange.model.Currency;
import com.currencyexchange.service.CurrencyService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;


import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@WebServlet("/currencies")
public class CurrenciesServlet extends AbstractServlet {
    private CurrencyService currencyService;

    @Override
    public void init() throws ServletException {
        super.init();
        currencyService = (CurrencyService) getServletContext().getAttribute("currencyService");
        if (currencyService == null) {
            throw new IllegalStateException("currencyService not initialized in servlet context");
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Currency> currencies = currencyService.findAllCurrencies();
        List<CurrencyResponseDto> responseList = currencies.stream()
                .map(CurrencyMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
        writeJson(resp, responseList, HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String code = req.getParameter("code");
        String fullName = req.getParameter("name");
        String sign = req.getParameter("sign");

        code = (code != null) ? code.trim() : null;
        fullName = (fullName != null) ? fullName.trim() : null;
        sign = (sign != null) ? sign.trim() : null;

        CurrencyRequestDto requestDto = new CurrencyRequestDto(code, fullName, sign);
        Currency currency = new Currency(requestDto.code(), requestDto.name(), requestDto.sign());
        Currency created = currencyService.createCurrency(currency);
        CurrencyResponseDto responseDto = CurrencyMapper.INSTANCE.toDto(created);
        writeJson(resp, responseDto, HttpServletResponse.SC_CREATED);
    }
}