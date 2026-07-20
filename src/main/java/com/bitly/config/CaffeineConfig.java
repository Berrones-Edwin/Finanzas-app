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

    @Bean
    public CacheManager cacheManager() {

        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        cacheManager.registerCustomCache("dashboard-by-account", Caffeine.newBuilder()
                .maximumSize(1)
                .expireAfterWrite(2, TimeUnit.MINUTES)
                .build());

        cacheManager.registerCustomCache("dashboard-by-category", Caffeine.newBuilder()
                .maximumSize(1)
                .expireAfterWrite(2, TimeUnit.MINUTES)
                .build());
        cacheManager.registerCustomCache("dashboard-montly-trends", Caffeine.newBuilder()
                .maximumSize(1)
                .expireAfterWrite(2, TimeUnit.MINUTES)
                .build());
        cacheManager.registerCustomCache("dashboard-summary", Caffeine.newBuilder()
                .maximumSize(1)
                .expireAfterWrite(2, TimeUnit.MINUTES)
                .build());

        return cacheManager;
    }

    @Override
    public CacheErrorHandler errorHandler() {

        return new CustomCacheErrorHandler();
    }
}
