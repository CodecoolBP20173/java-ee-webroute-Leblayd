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
    private final WebRoute.Method method;
    private final Method handler;
    private Map<Integer, Entry<String, Object>> baseParameters;

    Handler(Method handler, WebRoute.Method method) {
        this.handler = handler;
        this.method = method;
    }

    Handler(Method handler, WebRoute.Method method, Map<Integer, Entry<String, Object>> baseParameters) {
        this(handler, method);
        this.baseParameters = baseParameters;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = callRouteHandler(exchange);

        try (OutputStream output = exchange.getResponseBody()) {
            output.write(response.getBytes());
        }
    }

    private String callRouteHandler(HttpExchange exchange) throws IOException {
        String response;
        try {
            Map<String, Object> parameters = getParametersFromURI(exchange.getRequestURI().getPath());

            response = call(exchange, parameters);
            exchange.sendResponseHeaders(200, response.length());
        } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
            response = "500 Internal Server Error:\n\n" + e.getMessage();
            exchange.sendResponseHeaders(500, response.length());
        }
        return response;
    }

    private String call(HttpExchange exchange, Map<String, Object> parameters)
            throws InvocationTargetException, IllegalAccessException, IllegalArgumentException {
        if (parameters == null) {
            return (String) handler.invoke(null, exchange, parameters);
        } else {
            return (String) handler.invoke(null, exchange);
        }
    }

    /**
     * Only extracts the first X parameters,
     * where X is the number of parameters the route was set up with
     *
     * @param path the current page's URI path
     * @return a map of parameters extracted from the path
     */
    private Map<String, Object> getParametersFromURI(String path) {
        if (baseParameters == null) return null;
        Map<String, Object> parameters = new HashMap<>();
        for (int i = 0; i < baseParameters.entrySet().size(); i++) {
            Entry<String, Object> map = baseParameters.get(i);
            map.setValue(path.split("/")[i + 2]);
            parameters.put(map.getKey(), map.getValue());
        }
        return parameters;
    }
}
