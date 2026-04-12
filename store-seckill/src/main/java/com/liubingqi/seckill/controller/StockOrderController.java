package com.liubingqi.seckill.controller;

import cn.hutool.crypto.digest.mac.MacEngine;
import com.liubingqi.common.domain.Result;
import com.liubingqi.common.exception.BusinessException;
import com.liubingqi.seckill.domain.dto.SeckillCreateOrderDto;
import com.liubingqi.seckill.domain.dto.StockDto;
import com.liubingqi.seckill.service.IStockOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seckill/order")
@Tag(name = "秒杀服务-下单服务", description = "")
public class StockOrderController {

    private final IStockOrderService stockOrderService;


    /**
     *  扣减库存-redis-mq通知订单服务生成订单
     * @param dto
     * @return
     */
    @PostMapping()
    @Operation(summary = "扣减库存-redis-mq通知订单服务生成订单")
    public Result<String> stockOrder(
            @RequestHeader("X-SeckillOrder-Token") String orderToken,
            @RequestBody SeckillCreateOrderDto dto){
       if (orderToken == null || orderToken.isBlank()) {
           throw new BusinessException("此次为非法下单");
       }
       if (dto.getNum() != 1){
           throw new BusinessException("非法下单,数量大于1");
       }
        String message = stockOrderService.stockOrder(dto, orderToken);
        return Result.success(message);
    }


    /**
     *  生成用户下单token --- 防重复下单
     * @return
     */
    @PostMapping("/getToken")
    @Operation(summary = "生成用户下单token --- 防重复下单")
    public Result<String> gettoken(@RequestBody StockDto dto){
        return stockOrderService.gettoken(dto);
    }
}
