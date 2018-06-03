package com.codecool.leblayd.webroute;

import com.sun.net.httpserver.HttpExchange;

import java.util.Map;

import static com.codecool.leblayd.webroute.WebRoute.Method.POST;

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
        System.out.println("GET REQUEST RECEIVED!");
        return "other route successful";
    }

    @WebRoute(path = "/other", request = POST)
    public static String OtherPost(HttpExchange exchange) {
        System.out.println("POST REQUEST RECEIVED!");
        return "POST request successful";
    }

    @WebRoute(path = "/user/<username>/<page>")
    public static String UserPage(HttpExchange exchange, Map<String, Object> params) {
        return "user: " + params.get("username") + "'s " + params.get("page") + " page";
    }
}
