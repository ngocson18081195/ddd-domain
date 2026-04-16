package com.xxx.ddd.controller.http;

import com.xxx.ddd.application.service.model.TicketDetailDTO;
import com.xxx.ddd.application.service.ticket.TicketDetailAppService;
import com.xxx.ddd.controller.model.enums.ResultUtil;
import com.xxx.ddd.controller.model.vo.ResultMessage;
import com.xxx.ddd.domain.model.entity.TicketDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ticket")
@Slf4j
public class TicketDetailController {

    // CALL Service Application
    @Autowired
    private TicketDetailAppService ticketDetailAppService;

    @GetMapping("/{ticketId}/{quantity}/cas")
    public boolean getTicketByLevel3(
            @PathVariable("ticketId") Long ticketId,
            @PathVariable("quantity") int quantity
    ) {
        return ticketDetailAppService.decreaseStockLevel3(ticketId, quantity);

    }

    @GetMapping("/{ticketId}/{quantity}/order")
    public boolean getTicketByLevel(
            @PathVariable("ticketId") Long ticketId,
            @PathVariable("quantity") int quantity
    ) {
        return ticketDetailAppService.decreaseStockLevel(ticketId, quantity);
    }

    @GetMapping("/{ticketId}/detail/{detailId}")
    public ResultMessage<TicketDetailDTO> getTicketDetail(
            @PathVariable("ticketId") Long ticketId,
            @PathVariable("detailId") Long detailId,
            @RequestParam(name = "version", required = false) Long version
    ) {
        return ResultUtil.data(ticketDetailAppService.getTicketDetailById(detailId, version));
    }

    @GetMapping("/order/detail/{detailId}")
    public boolean orderTicketByUser(
            @PathVariable("ticketId") Long ticketId,
            @PathVariable("detailId") Long detailId
    ) {
        return ticketDetailAppService.orderTicketByUser(ticketId);
    }

}
