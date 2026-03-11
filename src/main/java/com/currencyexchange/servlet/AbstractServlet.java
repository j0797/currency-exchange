package com.currencyexchange.servlet;

import com.currencyexchange.dto.ErrorResponseDto;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public abstract class AbstractServlet extends HttpServlet {
    protected final Gson gson = new Gson();

    protected void sendError(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(gson.toJson(new ErrorResponseDto(message)));
    }

    protected void writeJson(HttpServletResponse resp, Object data, int status) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(gson.toJson(data));
    }
}