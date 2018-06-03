package com.codecool.leblayd.webroute;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface WebRoute {
    String path() default "/";
    Method request() default Method.GET;

    enum Method {
        GET, POST, PUT, DELETE;

        static Method getFromString(String string) {
            for (Method method : Method.values()) {
                if (method.toString().equals(string)) {
                    return method;
                }
            }
            return null;
        }
    }
}
