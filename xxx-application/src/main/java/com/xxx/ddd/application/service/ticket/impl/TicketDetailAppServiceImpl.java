package com.xxx.ddd.application.service.ticket.impl;

import com.xxx.ddd.application.mapper.TicketDetailMapper;
import com.xxx.ddd.application.service.cache.StockOrderCacheService;
import com.xxx.ddd.application.service.model.TicketDetailDTO;
import com.xxx.ddd.application.service.model.cache.TicketDetailCache;
import com.xxx.ddd.application.service.ticket.TicketDetailAppService;
import com.xxx.ddd.application.service.ticket.cache.TicketDetailCacheService;
import com.xxx.ddd.domain.repository.TicketOrderRepository;
import com.xxx.ddd.domain.service.TicketDetailDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TicketDetailAppServiceImpl implements TicketDetailAppService {

    // CALL Service Domain Module
    private final TicketOrderRepository ticketOrderRepository;

    // CALL CACHE
    private final TicketDetailCacheService ticketDetailCacheService;

    private final StockOrderCacheService stockOrderCacheService;

    public TicketDetailAppServiceImpl(TicketOrderRepository ticketOrderRepository, TicketDetailCacheService ticketDetailCacheService, StockOrderCacheService stockOrderCacheService) {
        this.ticketOrderRepository = ticketOrderRepository;
        this.ticketDetailCacheService = ticketDetailCacheService;
        this.stockOrderCacheService = stockOrderCacheService;
    }


    @Override
    public TicketDetailDTO getTicketDetailById(Long ticketId, Long version) {
        log.info("Implement Application : {}, {}", ticketId, version);

        TicketDetailCache ticketDetailCache = ticketDetailCacheService.getTicketDefaultCacheLocal(ticketId, version);
        //mapper to DTO
        TicketDetailDTO ticketDetailDTO = TicketDetailMapper.mapperToTicketDetailDTO(ticketDetailCache.getTicketDetail());
        ticketDetailDTO.setVersion(ticketDetailCache.getVersion());
        return ticketDetailDTO;

    }

    @Override
    public boolean orderTicketByUser(Long ticketId) {


        return ticketDetailCacheService.orderTicketByUser(ticketId);
    }

    @Override
    public boolean decreaseStockLevel(Long ticketId, int quantity) {
        int stockAvailable = ticketOrderRepository.getStockAvailable(ticketId);
        if (stockAvailable < quantity) {
            log.info("Case: stockAvailable < quantity | {}, {}", stockAvailable, quantity);
            return false;
        }
        return ticketOrderRepository.decreaseStockLevel1(ticketId, quantity);
    }

    @Override
    public boolean decreaseStockLevel3(Long ticketId, int quantity) {
//        int oldStockAvailable = ticketOrderRepository.getStockAvailable(ticketId);
//        if (oldStockAvailable < quantity) {
//            log.info("Case: oldStockAvailable < quantity | {}, {}", oldStockAvailable, quantity);
//            return false;
//        }
        // Check ticket in db
//        int oldStockAvailable = ticketOrderRepository.getStockAvailable(ticketId);
//        if (oldStockAvailable == 0) {
//            log.info("oldStockAvailable < quantity | {}, {}", oldStockAvailable, quantity);
//            return false;
//        }
//        return ticketOrderRepository.decreaseStockLevel3CAS(ticketId, oldStockAvailable, quantity);
        // Check ticket in Redis
//        int oldStockAvailable = stockOrderCacheService.decreaseStockOrder(ticketId, quantity);
        int oldStockAvailable = stockOrderCacheService.decreaseStockCacheByLUA(ticketId, quantity);

        if (oldStockAvailable == 0) {
            log.info("Case: oldStockAvailable < quantity | {}, {}", oldStockAvailable, quantity);
            return false;
        }
        return ticketOrderRepository.decreaseStockLevel1(ticketId, quantity);
    }

}
