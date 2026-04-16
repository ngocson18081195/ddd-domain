package com.xxx.ddd.application.service.ticket;


import com.xxx.ddd.application.service.model.TicketDetailDTO;
import com.xxx.ddd.application.service.model.cache.TicketDetailCache;
import com.xxx.ddd.domain.model.entity.TicketDetail;

public interface TicketDetailAppService {

    TicketDetailDTO getTicketDetailById(Long ticketId, Long version); // should convert to TickDetailDTO by Application Module

    boolean orderTicketByUser(Long ticketId);

    boolean decreaseStockLevel(Long ticketId, int quantity);

    boolean decreaseStockLevel3(Long ticketId, int quantity);

}
