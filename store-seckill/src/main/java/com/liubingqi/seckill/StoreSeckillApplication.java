package com.liubingqi.seckill;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 秒杀服务启动类
 * 功能：高并发秒杀、库存预热、分布式锁
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.liubingqi.seckill.mapper")
@ComponentScan(basePackages = {"com.liubingqi.common", "com.liubingqi.seckill"})
public class StoreSeckillApplication {

    public static void main(String[] args) {
        SpringApplication.run(StoreSeckillApplication.class, args);
    }

}
