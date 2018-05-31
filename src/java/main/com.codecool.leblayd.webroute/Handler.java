package com.codecool.leblayd.webroute;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

class Handler implements HttpHandler {
    private Method method;
    private Map<Integer, Entry<String, Object>> params;
    private Map<String, Object> simpleParams;

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
                getUrlParams(exchange.getRequestURI().getPath());
                response = (String) method.invoke(null, exchange, simpleParams);
            }
            exchange.sendResponseHeaders(200, response.length());
        } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
            response = "500 Internal Server Error:\n\n" + e.getMessage();
            exchange.sendResponseHeaders(500, response.length());
        }
        return response;
    }

    private void getUrlParams(String path) {
        simpleParams = new HashMap<>();
        for (int i = 0; i < params.entrySet().size(); i++) {
            Entry<String, Object> map = params.get(i);
            map.setValue(path.split("/")[i + 2]);
            simpleParams.put(map.getKey(), map.getValue());
        }
    }

    Handler(Method method) {
        this.method = method;
    }

    Handler(Method method, Map<Integer, Map.Entry<String, Object>> params) {
        this.method = method;
        this.params = params;
    }


}
