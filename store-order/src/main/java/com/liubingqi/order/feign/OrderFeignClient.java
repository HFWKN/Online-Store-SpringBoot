package com.liubingqi.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 订单服务Feign客户端
 */
@FeignClient(name = "store-order", path = "/order")
public interface OrderFeignClient {

    /**
     * 根据订单ID获取订单信息
     */
    @GetMapping("/{orderId}")
    Object getOrderById(@PathVariable("orderId") Long orderId);
}
