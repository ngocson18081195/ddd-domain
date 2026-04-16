package com.xxx.ddd.infrastructure.cache.redis;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Optional;

@Component
public class RedisInfrasServiceImpl implements RedisInfrasService {

    private static final Logger log = LoggerFactory.getLogger(RedisInfrasServiceImpl.class);
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void setInt(String key, int value) {
        if (!StringUtils.hasLength(key)) {
            return;
        }
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void setString(String key, String value) {
        if (StringUtils.hasLength(key)) {
            return;
        }
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public String getString(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key)).map(String::valueOf).orElse(null);
    }

    @Override
    public void setObject(String key, Object value) {
        if (!StringUtils.hasLength(key)) {
            return;
        }
        try {
            redisTemplate.opsForValue().set(key, value);
        }catch (Exception e) {
            log.info("set Object: {}", e.getMessage());
        }
    }

    @Override
    public <T> T getObject(String key, Class<T> targetClass) {
        Object result = redisTemplate.opsForValue().get(key);
        log.info("get Cache:: {} ", result);
        if (result == null) {
            return null;
        }
        if (result instanceof Map) {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.convertValue(result, targetClass);
        }
        if (result instanceof String) {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.convertValue(result, targetClass);
        }
        return null;
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public Integer getInt(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key)).map(o -> Integer.valueOf(o.toString())).orElse(0);
    }

    @Override
    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }
}
