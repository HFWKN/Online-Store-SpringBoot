package com.liubingqi.seckill.service;

import com.liubingqi.common.domain.Result;
import com.liubingqi.seckill.domain.dto.StockDto;
import com.liubingqi.seckill.domain.po.Stock;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liubingqi.seckill.domain.vo.StockVo;

/**
 * <p>
 * 商品库存表(秒杀核心) 服务类
 * </p>
 *
 * @author lbq
 * @since 2026-03-17
 */
public interface IStockService extends IService<Stock> {

    // 查询活动商品的规格信息
    Result<StockVo> listSpec(StockDto stockDto);

    // 预热活动商品的规格库存到 Redis
    Result<Void> getStockNum(StockDto stockDto);

    // 扣减秒杀活动商品库存
    Result<Void> deductStock(StockDto stockDto);
}
