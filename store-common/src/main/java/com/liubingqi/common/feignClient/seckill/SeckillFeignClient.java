package com.liubingqi.common.feignClient.seckill;


import com.liubingqi.common.domain.Result;
import com.liubingqi.common.domain.mq.SeckillOrderMessage;
import com.liubingqi.common.feignClient.seckill.dto.StockDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "store-seckill", path = "/seckill")
public interface SeckillFeignClient {

    /**
     *  扣减秒杀活动商品库存
     *  dto需要活动id，商品id，规格id，数量写死为1.
     */
    @PostMapping("/deductStock")
    Result<Void> deductStock(@RequestBody StockDto stockDto);

    /**
     * 死信补偿：回补秒杀库存并释放限购占位
     */
    @PostMapping("/compensateStock")
    Result<Void> compensateStock(@RequestBody SeckillOrderMessage message);
}
