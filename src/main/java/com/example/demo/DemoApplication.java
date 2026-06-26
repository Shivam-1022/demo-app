package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class DemoApplication {

    @GetMapping("/")
    public String home() {
        return "Application deployed through Jenkins + Docker + K3s";
    }

    @GetMapping("/health")
    public String health() {
        return "Application is healthy";
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}