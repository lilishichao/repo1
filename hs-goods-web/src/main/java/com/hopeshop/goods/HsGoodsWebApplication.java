package com.hopeshop.goods;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Created by silence on 2020/1/11.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class HsGoodsWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(HsGoodsWebApplication.class, args);
    }
}
