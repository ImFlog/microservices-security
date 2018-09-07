package com.github.imflog.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableZuulProxy
public class ApplicationInitializer {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationInitializer.class, args);
    }
}