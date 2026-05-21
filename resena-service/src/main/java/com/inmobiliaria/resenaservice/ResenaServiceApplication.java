package com.inmobiliaria.resenaservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ResenaServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResenaServiceApplication.class, args);
    }
}