package com.liubingqi.product.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 商品服务Feign客户端
 */
@FeignClient(name = "store-product", path = "/product")
public interface ProductFeignClient {

    /**
     * 根据商品ID获取商品信息
     */
    @GetMapping("/{productId}")
    Object getProductById(@PathVariable("productId") Long productId);
}
