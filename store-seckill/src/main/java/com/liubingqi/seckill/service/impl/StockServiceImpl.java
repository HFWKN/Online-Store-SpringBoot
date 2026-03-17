package com.liubingqi.seckill.service.impl;

import com.liubingqi.seckill.domain.po.Stock;
import com.liubingqi.seckill.mapper.StockMapper;
import com.liubingqi.seckill.service.IStockService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品库存表(秒杀核心) 服务实现类
 * </p>
 *
 * @author lbq
 * @since 2026-03-17
 */
@Service
public class StockServiceImpl extends ServiceImpl<StockMapper, Stock> implements IStockService {

}
