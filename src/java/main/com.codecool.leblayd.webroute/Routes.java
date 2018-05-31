package com.codecool.leblayd.webroute;

import com.sun.net.httpserver.HttpExchange;

import java.util.Map;

class Routes {
    @WebRoute(path = "/index")
    public static String Index(HttpExchange exchange) {

        return "<!doctype html>" +
                "<html lang=\"en\">" +
                "<head><title>Main page</title></head>" +
                "<body><h1>Welcome!</h1></body>" +
                "</html>";
    }

    @WebRoute(path = "/other")
    public static String Other(HttpExchange exchange) {

        return "other route successful";
    }

    @WebRoute(path = "/user/<username>/<subpage>")
    public static String UserPage(HttpExchange exchange, Map<String, Object> params) {
        return "user: " + params.get("username") + "'s " + params.get("subpage") + " page";
    }
}
