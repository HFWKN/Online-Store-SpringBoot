package com.liubingqi.seckill.service;

import com.liubingqi.common.domain.Result;
import com.liubingqi.seckill.domain.po.StockProduct;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liubingqi.seckill.domain.vo.StockProductVo;

import java.util.List;

/**
 * <p>
 * 秒杀商品信息表（列表/详情基础信息） 服务类
 * </p>
 *
 * @author lbq
 * @since 2026-04-08
 */
public interface IStockProductService extends IService<StockProduct> {


    // 获取当前活动秒杀商品列表
    Result<List<StockProductVo>> listByAcId(Long activityId);
}
