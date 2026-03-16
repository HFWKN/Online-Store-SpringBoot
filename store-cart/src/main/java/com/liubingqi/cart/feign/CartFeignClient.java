package com.liubingqi.cart.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 购物车服务Feign客户端
 */
@FeignClient(name = "store-cart", path = "/cart")
public interface CartFeignClient {

    /**
     * 获取用户购物车信息
     */
    @GetMapping("/user/{userId}")
    Object getCartByUserId(@PathVariable("userId") Long userId);
}
