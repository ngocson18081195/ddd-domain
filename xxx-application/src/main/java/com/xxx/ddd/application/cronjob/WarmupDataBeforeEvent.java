package com.xxx.ddd.application.cronjob;

import com.xxx.ddd.application.service.cache.StockOrderCacheService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WarmupDataBeforeEvent {

    private final StockOrderCacheService stockOrderCacheService;

    public WarmupDataBeforeEvent(StockOrderCacheService stockOrderCacheService) {
        this.stockOrderCacheService = stockOrderCacheService;
    }

    @PostConstruct
    public void loadDataTicketItemOnce() {
        log.info("Load ticket item");
        stockOrderCacheService.addStockAvailableToCache(4L);
    }
}
