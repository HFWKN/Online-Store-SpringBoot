package com.liubingqi.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 网关配置类
 * 注意：这里的路由配置会与 Nacos 中的配置合并
 * 建议统一使用 Nacos 配置，这里仅作为示例
 */
@Configuration
public class GatewayConfig {

    /**
     * 代码方式配置路由（可选）
     * 实际项目中建议使用 Nacos 动态配置
     */
    // @Bean
    // public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
    //     return builder.routes()
    //             .route("store-auth", r -> r
    //                     .path("/auth/**")
    //                     .filters(f -> f.stripPrefix(1))
    //                     .uri("lb://store-auth"))
    //             .build();
    // }
}
