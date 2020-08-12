package com.hopeshop.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * Created by silence on 2020/1/18.
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.hopeshop.user.mapper")
public class HsUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(HsUserApplication.class, args);
    }
}
