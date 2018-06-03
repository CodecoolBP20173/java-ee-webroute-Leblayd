package com.codecool.leblayd.webroute;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {
    private Map<String, Handler> routeHandlers = new HashMap<>();
    private HttpServer httpServer;
    private Class<?> routesClass;
    private int port;

    public Server(Class<?> routesClass, int port) {
        this.routesClass = routesClass;
        this.port = port;
    }

    public Server(Class<?> routesClass) {
        this(routesClass, 8000);
    }

    public void start() {
        try {
            this.httpServer = HttpServer.create(new InetSocketAddress(this.port), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.setUpRoutes();
        this.httpServer.setExecutor(null);
        this.httpServer.start();
        System.out.println("Server started");
    }

    private void setUpRoutes() {
        System.out.println("Creating all routes");
        for (Method handler : routesClass.getDeclaredMethods()) {
            if (handler.isAnnotationPresent(WebRoute.class)) {
                setUpContext(handler);
            }
        }
        System.out.println("Server routes successfully set up");
    }

    private void setUpContext(Method handler) {
        WebRoute annotation = handler.getAnnotation(WebRoute.class);
        String path = annotation.path();
        WebRoute.Method method = annotation.request();
        Map<Integer, Entry<String, Object>> baseParams = paramsFromPath(path);

        path = baseParams.isEmpty() ? path : path.substring(0, path.indexOf("<"));
        Handler routeHandler = new Handler(method, handler, baseParams);

        System.out.print("    route to path \"" + path + "\" ");
        Handler existingHandler;
        if ((existingHandler = this.routeHandlers.putIfAbsent(path, routeHandler)) == null) {
            this.httpServer.createContext(path, routeHandler);
            System.out.println("set up, handling " + method + " requests only");
        } else {
            existingHandler.addHandler(method, handler);
            System.out.println("modified, now handling " + method + " requests, too");
        }
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
