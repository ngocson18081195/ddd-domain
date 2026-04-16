package com.xxx.ddd.application.service.cache;

import com.xxx.ddd.infrastructure.cache.redis.RedisInfrasService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StockeOrderCacheService {

    private final RedisInfrasService redisInfrasService;

    @Autowired
    public StockeOrderCacheService(RedisInfrasService redisInfrasService) {
        this.redisInfrasService = redisInfrasService;
    }

    public int decreaseStockOrder(Long ticket, Integer quantity) {
        // 1. Get Stock Available in Redis
        String keyStockNormal = getKeyStockItemCache(ticket);
        int stockAvailable = redisInfrasService.getInt(keyStockNormal);
        log.info("stockAvailable Normal: {}, {}, {}", keyStockNormal, stockAvailable, quantity);
        // 2. Decrease Stock
        if (stockAvailable >= quantity) {
            redisInfrasService.setInt(keyStockNormal, stockAvailable - quantity);
            log.info("stockAvailable racing...: {}", stockAvailable - quantity);
            return 1;
        }
        return 0;
    }

    private String getKeyStockItemCache(Long ticket) {
        return null;
    }
}
