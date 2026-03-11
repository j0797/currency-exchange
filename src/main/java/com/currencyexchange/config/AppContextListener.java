package com.currencyexchange.config;

import com.currencyexchange.dao.*;
import com.currencyexchange.service.*;
import com.google.gson.Gson;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        CurrencyDao currencyDao = new JdbcCurrencyDao();
        ExchangeRateDao exchangeRateDao = new JdbcExchangeRateDao();

        CurrencyService currencyService = new CurrencyService(currencyDao);
        ExchangeRateService exchangeRateService = new ExchangeRateService(exchangeRateDao, currencyService);
        ExchangeService exchangeService = new ExchangeService(exchangeRateService, currencyService);

        Gson gson = new Gson();

        context.setAttribute("currencyService", currencyService);
        context.setAttribute("exchangeRateService", exchangeRateService);
        context.setAttribute("exchangeService", exchangeService);
        context.setAttribute("gson", gson);
    }
}