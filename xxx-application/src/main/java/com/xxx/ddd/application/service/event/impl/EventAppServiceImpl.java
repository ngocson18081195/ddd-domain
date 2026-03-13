package com.xxx.ddd.application.service.event.impl;

import com.xxx.ddd.application.service.event.EventAppService;
import com.xxx.ddd.domain.service.HiDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventAppServiceImpl implements EventAppService {

//    @Autowired
//    private HiDomainService hiDomainService;

    @Override
    public String sayHi(String who) {
//        return hiDomainService.sayHi(who);
        return "test";
    }
}
