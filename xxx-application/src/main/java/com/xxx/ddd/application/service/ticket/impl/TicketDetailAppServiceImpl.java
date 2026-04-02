package com.xxx.ddd.application.service.ticket.impl;

import com.xxx.ddd.application.service.mapper.TicketDetailMapper;
import com.xxx.ddd.application.service.model.TicketDetailDTO;
import com.xxx.ddd.application.service.model.cache.TicketDetailCache;
import com.xxx.ddd.application.service.ticket.TicketDetailAppService;
import com.xxx.ddd.application.service.ticket.cache.TicketDetailCacheService;
import com.xxx.ddd.domain.model.entity.TicketDetail;
import com.xxx.ddd.domain.service.TicketDetailDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TicketDetailAppServiceImpl implements TicketDetailAppService {

    // CALL Service Domain Module
    @Autowired
    private TicketDetailDomainService ticketDetailDomainService;

    // CALL CACHE
    @Autowired
    private TicketDetailCacheService ticketDetailCacheService;

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

}
