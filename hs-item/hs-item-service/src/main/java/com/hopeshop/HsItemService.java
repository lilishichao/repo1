package com.hopeshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableEurekaClient
@MapperScan("com.hopeshop.item.mapper")

public class HsItemService {
    public static void main(String[] args) {
        SpringApplication.run(HsItemService.class,args);
    }
}
