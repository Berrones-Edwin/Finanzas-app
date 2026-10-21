package com.bitly.config;

import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bitly.exceptions.CustomCacheErrorHandler;
import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
public class CaffeineConfig implements CachingConfigurer {

    private Caffeine<Object, Object> dashboardCacheSpec() {
        return Caffeine.newBuilder()
                .maximumSize(1_000)
                .expireAfterWrite(2, TimeUnit.MINUTES);
    }

    @Bean
    public CacheManager cacheManager() {

        var cacheManager = new CaffeineCacheManager();
        cacheManager.registerCustomCache("dashboard-summary", dashboardCacheSpec().build());
        cacheManager.registerCustomCache("dashboard-by-account", dashboardCacheSpec().build());
        cacheManager.registerCustomCache("dashboard-by-category", dashboardCacheSpec().build());
        cacheManager.registerCustomCache("dashboard-monthly-trends", dashboardCacheSpec().build());

        return cacheManager;
    }

    @Override
    public CacheErrorHandler errorHandler() {

        return new CustomCacheErrorHandler();
    }
}
