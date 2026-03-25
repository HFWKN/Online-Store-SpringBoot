package com.liubingqi.cart;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 购物车服务启动类
 * 功能：购物车管理、商品加购、购物车查询
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.liubingqi.common.feignClient")
@MapperScan("com.liubingqi.cart.mapper")
@ComponentScan(basePackages = {"com.liubingqi.common", "com.liubingqi.cart"})
public class StoreCartApplication {

    public static void main(String[] args) {
        SpringApplication.run(StoreCartApplication.class, args);
    }

}
