package com.liubingqi.seckill.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liubingqi.common.domain.Result;
import com.liubingqi.common.exception.BusinessException;
import com.liubingqi.common.utils.RedisUtils;
import com.liubingqi.seckill.constants.SeckillRedisKeyConstants;
import com.liubingqi.seckill.domain.po.StockProduct;
import com.liubingqi.seckill.domain.vo.StockProductVo;
import com.liubingqi.seckill.mapper.StockProductMapper;
import com.liubingqi.seckill.service.IStockProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 秒杀商品信息表（列表/详情基础信息） 服务实现类
 * </p>
 *
 * @author lbq
 * @since 2026-04-08
 */
@Service
@RequiredArgsConstructor
public class StockProductServiceImpl extends ServiceImpl<StockProductMapper, StockProduct> implements IStockProductService {

    // 正常列表缓存 30 分钟，减少数据库压力。
    private static final long LIST_CACHE_TTL_MINUTES = 30L;
    // 空结果缓存 3 分钟，防止反复查不到还一直打 DB（缓存穿透）。
    private static final long EMPTY_LIST_CACHE_TTL_MINUTES = 3L;

    // 注入redis工具类
    private final RedisUtils redisUtils;

    /**
     *  获取当前活动秒杀商品列表
     * @param activityId
     * @return
     */
    @Override
    public Result<List<StockProductVo>> listByAcId(Long activityId) {
        if(activityId == null){
            throw new BusinessException("无该活动信息");
        }
        // 先查询缓存，判断缓存是否存在
        String cacheKey = SeckillRedisKeyConstants.SECKILL_LIST_KEY_PREFIX + activityId;
        String cacheValue = redisUtils.get(cacheKey);
        if (StrUtil.isNotBlank(cacheValue)) {
            // 解析缓存数据，转换成 StockProductVo 对象
            List<StockProductVo> cachedList = JSON.parseArray(cacheValue, StockProductVo.class);
            return Result.success(cachedList);
        }

        // 缓存不存在，则查询数据库
        // 查询当前活动中的商品
        List<StockProduct> productList = lambdaQuery()
                .eq(StockProduct::getActivityId, activityId)
                .list();
        // 如果商品列表为空，则返回空列表
        if (CollectionUtil.isEmpty(productList)){
            // 空列表短暂缓存，避免缓存穿透反复打库
            redisUtils.set(cacheKey, new ArrayList<>(), EMPTY_LIST_CACHE_TTL_MINUTES, TimeUnit.MINUTES);
            return Result.success(new ArrayList<>());
        }
        List<StockProductVo> productVoList = new ArrayList<>();
        for (StockProduct product : productList) {
            StockProductVo productVo = new StockProductVo();
            productVo.setProductId(product.getProductId());
            productVo.setImageUrl(product.getMainImage());
            productVo.setProductName(product.getProductName());
            productVo.setOriginPrice(product.getOriginPrice());
            productVoList.add(productVo);
        }
        // 添加商品信息到缓存
        // 缓存秒杀列表信息，减少首页高频请求对数据库的压力
        redisUtils.set(cacheKey, productVoList, LIST_CACHE_TTL_MINUTES, TimeUnit.MINUTES);

        return Result.success(productVoList);
    }
}
