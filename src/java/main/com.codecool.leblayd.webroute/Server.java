package com.codecool.leblayd.webroute;

import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
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

        for (java.lang.reflect.Method method : Routes.class.getDeclaredMethods()) {
            if (method.isAnnotationPresent(WebRoute.class)) {
                WebRoute annotation = method.getAnnotation(WebRoute.class);
                String path = annotation.path();
                WebRoute.Method request = annotation.request();
                Map<Integer, Entry<String, Object>> params = paramsFromPath(path);

                if (params.isEmpty()) {
                    Server.server.createContext(path, new Handler(method, request));
                } else {
                    path = path.substring(0, path.substring(1).indexOf("<"));
                    Server.server.createContext(path, new Handler(method, request, params));
                }
                System.out.println("    route to path \"" + path + "\" set up");
            }
        }

        System.out.println("Server routes successfully set up");
    }

    private static Map<Integer, Entry<String, Object>> paramsFromPath(String path) {
        Pattern p = Pattern.compile("<.*?>");
        Matcher m = p.matcher(path);
        Map<Integer, Entry<String, Object>> params = new HashMap<>();

        Integer i = 0;
        while (m.find()) {
            String param = m.group(0);
            params.put(i++, new SimpleEntry<>(param.substring(1, param.length() - 1), null));
        }
        return params;
    }
}
