package com.liubingqi.seckill.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.liubingqi.common.domain.Result;
import com.liubingqi.common.exception.BusinessException;
import com.liubingqi.common.feignClient.product.ProductFeignClient;
import com.liubingqi.common.feignClient.product.vo.ProductSpecVo;
import com.liubingqi.common.feignClient.product.vo.ProductVo;
import com.liubingqi.seckill.domain.po.Stock;
import com.liubingqi.seckill.domain.vo.StockVo;
import com.liubingqi.seckill.mapper.StockMapper;
import com.liubingqi.seckill.service.IStockService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 商品库存表(秒杀核心) 服务实现类
 * </p>
 *
 * @author lbq
 * @since 2026-03-17
 */
@Service
@RequiredArgsConstructor
public class StockServiceImpl extends ServiceImpl<StockMapper, Stock> implements IStockService {

    private final ProductFeignClient productFeignClient;

    /**
     *  查询活动商品的规格信息
     * @param productId
     * @return
     */
    @Override
    public Result<List<StockVo>> list(Long productId) {
        return null;
    }
}
