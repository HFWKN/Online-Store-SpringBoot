package com.liubingqi.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 认证服务启动类
 * 功能：用户登录、注册、JWT 生成与验证
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.liubingqi.auth.mapper")
public class StoreAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(StoreAuthApplication.class, args);
    }

}
