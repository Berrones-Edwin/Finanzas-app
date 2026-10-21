package com.bitly.config;

import java.lang.reflect.Method;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

@Component("dashboardKeyGenerator")
public class DashboardKeyGenerator implements KeyGenerator {

    private static final String SEPARATOR = "|";

    @Override
    public Object generate(Object target, Method method, Object... params) {

        return params[0] + SEPARATOR + params[1] + SEPARATOR + params[2];
    }

}
