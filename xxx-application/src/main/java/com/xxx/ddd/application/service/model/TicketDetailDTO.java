package com.xxx.ddd.application.service.model;

import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;

import java.util.Date;

@Data
public class TicketDetailDTO {

    private Long id;
    private String name;
    private String description;
    private int stockInitial;
    private int stockAvailable;
    private boolean isStockPrepared;
    private Long priceOriginal;
    private Long priceFlash;
    private Date saleStartTime;
    private Date saleEndTime;
    private int status;
    private Long activityId;
    private Long version;

}
