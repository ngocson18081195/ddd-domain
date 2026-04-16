package com.xxx.ddd.application.service.cache;

import com.xxx.ddd.domain.model.entity.TicketDetail;
import com.xxx.ddd.domain.service.TicketDetailDomainService;
import com.xxx.ddd.infrastructure.cache.redis.RedisInfrasService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@Slf4j
public class StockOrderCacheService {

    private final RedisInfrasService redisInfrasService;

    private final TicketDetailDomainService ticketDetailDomainService;

    @Autowired
    public StockOrderCacheService(RedisInfrasService redisInfrasService, TicketDetailDomainService ticketDetailDomainService) {
        this.redisInfrasService = redisInfrasService;
        this.ticketDetailDomainService = ticketDetailDomainService;
    }

    public int decreaseStockOrder(Long ticket, Integer quantity) {
        // 1. Get Stock Available in Redis
        String keyStockNormal = genEventItemKey(ticket);
        int stockAvailable = redisInfrasService.getInt(keyStockNormal);
        log.info("stockAvailable Normal: {}, {}, {}", keyStockNormal, stockAvailable, stockAvailable - quantity);
        // 2. Decrease Stock
        if (stockAvailable >= quantity) {
            redisInfrasService.setInt(keyStockNormal, stockAvailable - quantity);
            log.info("stockAvailable racing...: {}", stockAvailable - quantity);
            return 1;
        }
        return 0;
    }

    private String getKeyStockItemCache(Long ticket) {
        return redisInfrasService.getString(genEventItemKey(ticket));
    }

    public boolean addStockAvailableToCache(Long ticketId) {
        if (ticketId == null) {
            return false;
        }
        TicketDetail ticketDetailById = ticketDetailDomainService.getTicketDetailById(ticketId);
        if (ticketDetailById == null) {
            return false;
        }
        String keyStockItemCache = genEventItemKey(ticketId);
        if (keyStockItemCache == null) {
            redisInfrasService.setObject(genEventItemKey(ticketId), ticketDetailById.getStockAvailable());
            log.info("get -> getKeyStockItemCache() | {},  {}, {}", ticketId, keyStockItemCache, ticketDetailById.getStockAvailable());
            return true;
        }
        redisInfrasService.setInt(keyStockItemCache, ticketDetailById.getStockAvailable());
        return true;
    }

    private String genEventItemKey(Long itemId) {
        return "TICKET:" + itemId + ":STOCK";
    }

    public int decreaseStockCacheByLUA(Long ticket, Integer quantity) {
        String ketStockLUA = genEventItemKey(ticket);
        String luaScript = "local stock = tonumber(redis.call('GET', KEYS[1]))" +
                "if (stock >= tonumber(ARGV[1])) then" +
                "   redis.call('SET', KEYS[1], stock - tonumber(ARGV[1]))" +
                "   return 1;" +
                "end;" +
                "   return 0;";

        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(luaScript);
        redisScript.setResultType(Long.class);
        Long result = redisInfrasService.getRedisTemplate()
                .execute(redisScript, Collections.singletonList(ketStockLUA), quantity);
        log.info("LUA result: {}", result.intValue());
        return result.intValue();
    }
}
