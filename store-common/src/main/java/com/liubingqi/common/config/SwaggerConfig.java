package com.liubingqi.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j 配置类
 * 用于生成API文档，兼容Spring Boot 3.x
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("在线商城微服务API文档")
                        .description("高并发秒杀商城系统接口文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("liubingqi")
                                .email("")
                                .url("")));
    }
}
