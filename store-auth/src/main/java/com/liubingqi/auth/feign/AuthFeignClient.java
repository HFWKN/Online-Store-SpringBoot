package com.liubingqi.auth.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 认证服务Feign客户端
 */
@FeignClient(name = "store-auth", path = "/auth")
public interface AuthFeignClient {

    /**
     * 验证token
     */
    @GetMapping("/validate")
    Boolean validateToken(@RequestParam("token") String token);
}
