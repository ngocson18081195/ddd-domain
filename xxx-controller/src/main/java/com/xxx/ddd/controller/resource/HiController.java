package com.xxx.ddd.controller.resource;

import com.xxx.ddd.application.service.event.EventAppService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;

@RestController
@RequestMapping("/hello")
public class HiController {

    @Autowired
    private EventAppService eventAppService;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/hi")
    @RateLimiter(name = "backendA", fallbackMethod = "fallbackHello")
    public String sayHi() {
        return eventAppService.sayHi("son");
    }

    public String fallbackHello(Throwable throwable) {
        return "Too many request";
    }

    @GetMapping("/hi/v1")
    @RateLimiter(name = "backendB", fallbackMethod = "fallbackHello")
    public String hello() {
        return eventAppService.sayHi("son1");
    }

    private static final SecureRandom random = new SecureRandom();
    @GetMapping("/ciruit/breaker")
    @CircuitBreaker(name = "checkRandom", fallbackMethod = "fallbackBreaker")
    public String breaker() {
        String url = "https://fakestoreapi.com/products/" + random.nextInt(20);

        return restTemplate.getForObject(url, String.class);
    }

    public String fallbackBreaker(Throwable throwable) {
        return "Fake fakestoreapi error";
    }
}
