package com.liubingqi.cart;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 购物车服务启动类
 * 功能：购物车管理、商品加购、购物车查询
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.liubingqi.cart.mapper")
public class StoreCartApplication {

    public static void main(String[] args) {
        SpringApplication.run(StoreCartApplication.class, args);
    }

}
