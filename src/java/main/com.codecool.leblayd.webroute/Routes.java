package com.codecool.leblayd.webroute;

import com.sun.net.httpserver.HttpExchange;

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

    @WebRoute(path = "/user/<username>")
    public static String UserPage(HttpExchange exchange) {
        return "user: " + "PLACEHOLDER" + "'s page";
    }
}
