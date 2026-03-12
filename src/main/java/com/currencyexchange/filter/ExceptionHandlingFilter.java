package com.currencyexchange.filter;

import com.currencyexchange.dto.ErrorResponseDto;
import com.currencyexchange.exception.*;
import com.google.gson.Gson;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@WebFilter("/*")
public class ExceptionHandlingFilter implements Filter {

    private Gson gson;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        gson = (Gson) filterConfig.getServletContext().getAttribute("gson");
        if (gson == null) {
            throw new IllegalStateException("Gson not initialized in servlet context");
        }
        log.info("ExceptionHandlingFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PATCH, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setHeader("Access-Control-Max-Age", "3600");

        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            resp.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            handleException(e, resp);
        }
    }

    private void handleException(Exception e, HttpServletResponse resp) throws IOException {
        if (e instanceof AlreadyExistsException) {
            log.warn("Conflict: {}", e.getMessage());
            sendError(resp, HttpServletResponse.SC_CONFLICT, e.getMessage());
        } else if (e instanceof ValidationException) {
            log.warn("Validation error: {}", e.getMessage());
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } else if (e instanceof NotFoundException) {
            log.warn("Not found: {}", e.getMessage());
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } else if (e instanceof DatabaseException) {
            log.error("Database error: {}", e.getMessage(), e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        } else {
            log.error("Unexpected error: {}", e.getMessage(), e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    private void sendError(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(gson.toJson(new ErrorResponseDto(message)));
    }

    @Override
    public void destroy() {
        log.info("ExceptionHandlingFilter destroyed");
    }
}