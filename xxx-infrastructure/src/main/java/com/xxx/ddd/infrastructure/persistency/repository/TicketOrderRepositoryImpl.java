package com.xxx.ddd.infrastructure.persistency.repository;

import com.xxx.ddd.domain.repository.TicketOrderRepository;
import com.xxx.ddd.infrastructure.persistency.mapper.TicketOrderJPAMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TicketOrderRepositoryImpl implements TicketOrderRepository {

    private final TicketOrderJPAMapper ticketOrderJPAMapper;

    @Autowired
    public TicketOrderRepositoryImpl(TicketOrderJPAMapper ticketOrderJPAMapper) {
        this.ticketOrderJPAMapper = ticketOrderJPAMapper;
    }

    @Override
    public boolean decreaseStockLevel1(Long ticketId, int quantity) {
        log.info("Run test: decreaseStockLevel1 with: | {}, {}", ticketId, quantity);
        return ticketOrderJPAMapper.decreaseStockLevel1(ticketId, quantity) > 0;
    }

    @Override
    public boolean decreaseStockLevel3CAS(Long ticketId, int oldStockAvailable, int quantity) {
        log.info("Run test: decreaseStockLevel3CAS with: | {}, {}, {}", ticketId, oldStockAvailable, quantity);
        return ticketOrderJPAMapper.decreaseStockLevel3CAS(ticketId, oldStockAvailable, quantity) > 0;
    }

    @Override
    public int getStockAvailable(Long ticketId) {
        return ticketOrderJPAMapper.getStockAvailable(ticketId);
    }
}
