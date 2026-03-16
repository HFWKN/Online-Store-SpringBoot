package com.liubingqi.seckill.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 秒杀服务Feign客户端
 */
@FeignClient(name = "store-seckill", path = "/seckill")
public interface SeckillFeignClient {

    /**
     * 获取秒杀活动信息
     */
    @GetMapping("/activity/{activityId}")
    Object getSeckillActivity(@PathVariable("activityId") Long activityId);
}
