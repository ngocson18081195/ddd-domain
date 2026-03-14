package com.xxx;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@RequestMapping("/hello")
public class StartApplication {
    static void main(String[] args) {
//        System.out.println("Hello World");
        SpringApplication.run(StartApplication.class, args);
    }

    @GetMapping("/test")
    public String say() {
        return "test";
    }
}
