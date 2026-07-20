package com.bitly.exceptions;

import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomCacheErrorHandler implements CacheErrorHandler {

     @Override
    public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        log.warn("Error al LEER de la caché '{}' para la clave '{}'. Accediendo a la base de datos... Error: {}", 
                cache.getName(), key, exception.getMessage());
        // Al NO relanzar la excepción, Spring continúa ejecutando el método original (DB)
    }

    @Override
    public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
        log.warn("Error al ESCRIBIR en la caché '{}' para la clave '{}'. Error: {}", 
                cache.getName(), key, exception.getMessage());
    }

    @Override
    public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        log.error("Error al BORRAR (Evict) en la caché '{}' para la clave '{}'. ¡Peligro de datos obsoletos! Error: {}", 
                cache.getName(), key, exception.getMessage());
    }

    @Override
    public void handleCacheClearError(RuntimeException exception, Cache cache) {
        log.error("Error al LIMPIAR (Clear) por completo la caché '{}'. Error: {}", 
                cache.getName(), exception.getMessage());
    }

}
