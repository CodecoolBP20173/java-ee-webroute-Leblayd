package com.codecool.leblayd.webroute;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

class Handler implements HttpHandler {
    private final boolean debug = System.getenv("debug").equalsIgnoreCase("true");
    private final Map<WebRoute.Method, Method> handlers = new HashMap<>();
    private Map<Integer, Entry<String, Object>> baseParameters;

    Handler(WebRoute.Method method, Method handler) {
        this.handlers.putIfAbsent(method, handler);
    }

    Handler(WebRoute.Method method, Method handler, Map<Integer, Entry<String, Object>> baseParameters) {
        this(method, handler);
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
        } catch (Exception e) {
            e.printStackTrace();
            response = "500 Internal Server Error";
            if (debug) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                response = response + ":\n\n" + sw.toString();
            }
            exchange.sendResponseHeaders(500, response.length());
        }
        return response;
    }

    /**
     * @param exchange the HTTP Exchange object
     * @param parameters a Map of the parameters received from the URI,
     *                   if null, or empty then it won't try to pass them to the handler method
     * @return the response from the handler
     * @throws InvocationTargetException default and necessary
     * @throws IllegalAccessException default and necessary
     * @throws IllegalArgumentException thrown when the parameters don't match the called handler function
     */
    private String call(HttpExchange exchange, Map<?, ?> parameters)
            throws InvocationTargetException, IllegalAccessException, IllegalArgumentException {
        WebRoute.Method method = WebRoute.Method.getFromString(exchange.getRequestMethod());
        if (parameters == null || parameters.isEmpty()) {
            return (String) handlers.get(method).invoke(null, exchange);
        } else {
            return (String) handlers.get(method).invoke(null, exchange, parameters);
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
