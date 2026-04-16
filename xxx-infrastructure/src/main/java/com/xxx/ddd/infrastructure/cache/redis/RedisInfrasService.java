package com.xxx.ddd.infrastructure.cache.redis;

import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.RedisTemplate;

public interface RedisInfrasService {
    void setInt(String key, int value);

    void setString(String key, String value);
    String getString(String key);
    void setObject(String key, Object value);
    <T> T getObject(String key, Class<T> targetClass);

    //delete redis by key
    void delete(String key);
    Integer getInt(String key);

    RedisTemplate<String, Object> getRedisTemplate();
}
