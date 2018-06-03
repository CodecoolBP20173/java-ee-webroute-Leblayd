package com.codecool.leblayd.webroute;

public class TestServer {
    public static void main(String[] args) {
        Server server = new Server(TestRoutes.class);
        server.start();
    }
}
