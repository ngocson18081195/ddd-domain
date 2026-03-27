package com.xxx.ddd.controller.resource;

import com.xxx.ddd.application.service.ticket.TicketDetailAppService;
import com.xxx.ddd.domain.model.entity.TicketDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ticket")
@Slf4j
public class TicketDetailController {

    // CALL Service Application
    @Autowired
    private TicketDetailAppService ticketDetailAppService;

    @GetMapping("/{ticketId}/detail/{detailId}")
    public ResultMessage<TicketDetail> getTicketDetail(
            @PathVariable("ticketId") Long ticketId,
            @PathVariable("detailId") Long detailId
    ) {
        log.info("MEMBER TIPS GO");
        log.info(" ticketId:{}, detailId:{}", ticketId, detailId);
        return ResultUtil.data(ticketDetailAppService.getTicketDetailById(detailId));
    }
}
