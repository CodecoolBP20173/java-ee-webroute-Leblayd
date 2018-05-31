package com.codecool.leblayd.webroute;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

class Handler implements HttpHandler {
    private Method method;
    private Map<String, Object> params;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = callRouteHandler(exchange);

        try (OutputStream stream = exchange.getResponseBody()) {
            stream.write(response.getBytes());
        }
    }

    private String callRouteHandler(HttpExchange exchange) throws IOException {
        String response;
        try {
            if (params == null) {
                response = (String) method.invoke(null, exchange);
            } else {
                getUrlParams(exchange);
                response = (String) method.invoke(null, exchange, params);
            }
            exchange.sendResponseHeaders(200, response.length());
        } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
            response = "500 Internal Server Error:\n\n" + e.getMessage();
            exchange.sendResponseHeaders(500, response.length());
        }
        return response;
    }

    private void getUrlParams(HttpExchange exchange) {
        Map<String, Object> params = new HashMap<>();
        for (int i = 0; i < this.params.entrySet().size(); i++) {
            params.put((String) this.params.get(Integer.toString(i)),
                    exchange.getRequestURI().getPath().split("/")[i + 2]);
        }
        this.params = params;
    }

    Handler(Method method) {
        this.method = method;
    }

    Handler(Method method, Map<String, Object> params) {
        this.method = method;
        this.params = params;
    }


}
