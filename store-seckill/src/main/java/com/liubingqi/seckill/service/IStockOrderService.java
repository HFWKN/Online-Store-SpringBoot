package com.liubingqi.seckill.service;

import com.liubingqi.common.domain.Result;
import com.liubingqi.seckill.domain.dto.SeckillCreateOrderDto;
import com.liubingqi.seckill.domain.dto.StockDto;

public interface IStockOrderService {

    // 扣减库存-redis-mq通知订单服务生成订单
    String stockOrder(SeckillCreateOrderDto dto ,String orderToken);

    // 生成用户下单token --- 防重复下单
    Result<String> gettoken(StockDto dto);

}
