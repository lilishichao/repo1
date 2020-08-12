package com.hopeshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class HsUploadApplication {
    public static void main(String[] args) {
        SpringApplication.run(HsUploadApplication.class,args);
    }
}
