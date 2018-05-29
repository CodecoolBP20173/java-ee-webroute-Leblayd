package com.codecool.leblayd.webroute;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Tester {
    @WebRoute(bool = true)
    private static void doTrue() {
        System.out.println("inside the method");
    }

    public static void main(String[] args) {
        try {
            Method method = Tester.class.getDeclaredMethod("doTrue");
            method.invoke(null);
            WebRoute myAnnotation = method.getAnnotation(WebRoute.class);
            System.out.println("annotation string: " + myAnnotation.string());
            System.out.println("annotation bool: " + myAnnotation.bool());
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
