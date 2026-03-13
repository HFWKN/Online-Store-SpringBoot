package com.liubingqi.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 订单服务启动类
 * 功能：订单创建、支付、物流跟踪
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.liubingqi.order.mapper")
public class StoreOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(StoreOrderApplication.class, args);
    }

}
