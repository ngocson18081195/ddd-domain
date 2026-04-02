package com.xxx.ddd.infrastructure.cache.redis;

public interface RedisInfrasService {
    void setString(String key, String value);
    String getString(String key);
    void setObject(String key, Object value);
    <T> T getObject(String key, Class<T> targetClass);

    //delete redis by key
    void delete(String key);
}
