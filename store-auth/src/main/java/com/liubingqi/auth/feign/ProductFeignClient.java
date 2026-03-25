package com.liubingqi.auth.feign;


import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "store-auth", path = "/auth")
public class ProductFeignClient {
}
