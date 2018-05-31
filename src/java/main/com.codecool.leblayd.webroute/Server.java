package com.codecool.leblayd.webroute;

import com.sun.net.httpserver.HttpServer;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {
    private static HttpServer server;

    public static void main(String[] args) throws Exception {
        server = HttpServer.create(new InetSocketAddress(8000), 0);
        createRoutes();
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Server started");
    }

    private static void createRoutes() {
        System.out.println("Creating all routes");

        for (Method method : Routes.class.getDeclaredMethods()) {
            if (method.isAnnotationPresent(WebRoute.class)) {
                String path = method.getAnnotation(WebRoute.class).path();
                List<String> params = paramsFromPath(path); // TODO do stuff with the params
                Server.server.createContext(path, new Handler(method));

                System.out.println("    route to path \"" + path + "\" set up");
            }
        }

        System.out.println("Server routes successfully set up");
    }

    private static List<String> paramsFromPath(String path) {
        Pattern p = Pattern.compile("<.*?>");
        Matcher m = p.matcher(path);
        List<String> params = new ArrayList<>();

        while (m.find()) {
            String param = m.group(0);
            params.add(param.substring(1, param.length() - 1));
        }
        return params;
    }
}
