package com.liubingqi.seckill.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.liubingqi.common.domain.Result;
import com.liubingqi.common.domain.mq.SeckillOrderMessage;
import com.liubingqi.common.exception.BusinessException;
import com.liubingqi.common.feignClient.product.ProductFeignClient;
import com.liubingqi.common.feignClient.product.vo.ProductSpecVo;
import com.liubingqi.seckill.constants.SeckillRedisKeyConstants;
import com.liubingqi.seckill.domain.dto.StockDto;
import com.liubingqi.seckill.domain.po.Activity;
import com.liubingqi.seckill.domain.po.Stock;
import com.liubingqi.seckill.domain.vo.StockSpecVo;
import com.liubingqi.seckill.domain.vo.StockVo;
import com.liubingqi.seckill.mapper.StockMapper;
import com.liubingqi.seckill.service.IActivityService;
import com.liubingqi.seckill.service.IStockService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
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
    // 强类型注入
    @Qualifier("redisTemplate")
    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final IActivityService activityService;
    private final RedissonClient redissonClient;

    /**
     *  查询活动商品的规格信息
     * @param stockDto
     * @return
     */
    @Override
    public Result<StockVo> listSpec(StockDto stockDto) {
        Long activityId = stockDto.getActivityId();
        Long productId = stockDto.getProductId();

        // 先查询缓存，判断该商品的规格缓存是否存在
        // 商品信息缓存
        String specKey = SeckillRedisKeyConstants.SECKILL_SPEC_KEY_PREFIX + activityId + ":" + productId;

        // 判断缓存是否存在
        // 获取商品缓存
        Object productCaChe = redisTemplate.opsForValue().get(specKey);
        if (productCaChe != null) {
            StockVo cacheVo = productCaChe instanceof StockVo
                    ? (StockVo) productCaChe
                    : BeanUtil.toBean(productCaChe, StockVo.class);
            return Result.success(cacheVo);
        }
        String lockKey = SeckillRedisKeyConstants.SECKILL_SPEC_LOCK_KEY_PREFIX + activityId + ":" + productId;
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked = false;
        try {
            locked = lock.tryLock(200, 5000, TimeUnit.MILLISECONDS);
            if (!locked) {
                Object waitCache = redisTemplate.opsForValue().get(specKey);
                if (waitCache != null) {
                    StockVo cacheVo = waitCache instanceof StockVo
                            ? (StockVo) waitCache
                            : BeanUtil.toBean(waitCache, StockVo.class);
                    return Result.success(cacheVo);
                }
                throw new BusinessException("系统繁忙，请稍后重试");
            }

            // 判断缓存是否存在
            Object doubleCheckCache = redisTemplate.opsForValue().get(specKey);
            if (doubleCheckCache != null) {
                StockVo cacheVo = doubleCheckCache instanceof StockVo
                        ? (StockVo) doubleCheckCache
                        : BeanUtil.toBean(doubleCheckCache, StockVo.class);
                return Result.success(cacheVo);
            }

            // 如果缓存不存在，则查询数据库
            List<Stock> stockList = lambdaQuery()
                    .eq(Stock::getProductId, productId)
                    .eq(Stock::getActivityId, activityId)
                    .list();
            if(CollectionUtil.isEmpty(stockList)){
                throw new BusinessException("没有该商品");
            }

            List<Long> specIdList = stockList.stream()
                    .map(Stock::getProductSpecId)
                    .collect(Collectors.toList());
            List<ProductSpecVo> specList = productFeignClient.getBySpecIds(specIdList).getData();
            if (CollectionUtil.isEmpty(specList)){
                throw new BusinessException("该商品下没有规格信息");
            }

            List<StockSpecVo> specVoList = BeanUtil.copyToList(specList, StockSpecVo.class);

            StockVo vo = new StockVo();
            vo.setProductId(productId);
            vo.setProductName(stockDto.getProductName());
            vo.setImageUrl(stockDto.getImageUrl());
            vo.setSpecList(specVoList);

            long ttlSeconds = resolveCacheTtlSeconds(activityId);
            redisTemplate.opsForValue().set(specKey, vo, ttlSeconds, TimeUnit.SECONDS);

            return Result.success(vo);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("系统繁忙，请稍后重试");
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }


    /**
     *  预热活动商品的规格库存到 Redis
     * @param stockDto
     * @return
     */
    @Override
    public Result<Void> getStockNum(StockDto stockDto) {
        Long activityId = stockDto.getActivityId();
        Long productId = stockDto.getProductId();

        String lockKey = SeckillRedisKeyConstants.SECKILL_WARMUP_STOCK_LOCK_KEY_PREFIX + activityId + ":" + productId;
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked = false;
        try {
            locked = lock.tryLock(100, 8000, TimeUnit.MILLISECONDS);
            if (!locked) {
                return Result.success();
            }

            List<Stock> stockNumList = lambdaQuery()
                    .eq(Stock::getActivityId, activityId)
                    .eq(Stock::getProductId, productId)
                    .list();
            if(CollectionUtil.isEmpty(stockNumList)){
                throw new BusinessException("没有该商品的规格或库存信息");
            }
            long ttlSeconds = resolveCacheTtlSeconds(activityId);
            for (Stock s : stockNumList) {
                /*SpecNum specNum = new SpecNum();
                specNum.setStockId(s.getId());
                specNum.setSpecId(s.getProductSpecId());
                specNum.setProductId(s.getProductId());
                specNum.setNum(s.getAvailableStock());*/
                Integer specNum = s.getAvailableStock();
                String specNumKey = SeckillRedisKeyConstants.SECKILL_NUM_KEY_PREFIX + activityId + ":"+ productId + ":"+ s.getProductSpecId();
                stringRedisTemplate.opsForValue().set(specNumKey, String.valueOf(specNum), ttlSeconds, TimeUnit.SECONDS);
            }
            return Result.success();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("系统繁忙，请稍后重试");
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }


    /**
     *  扣减秒杀活动商品库存
     * @param stockDto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deductStock(StockDto stockDto) {
        if (stockDto == null
                || stockDto.getActivityId() == null
                || stockDto.getProductId() == null
                || stockDto.getSpecId() == null) {
            throw new BusinessException("扣减秒杀库存参数不完整");
        }

        // 条件更新 + 原子减1，避免超卖
        boolean update = lambdaUpdate()
                .eq(Stock::getActivityId, stockDto.getActivityId())
                .eq(Stock::getProductId, stockDto.getProductId())
                .eq(Stock::getProductSpecId, stockDto.getSpecId())
                .gt(Stock::getAvailableStock, 0)
                .setSql("available_stock = available_stock - 1")
                .update();
        if (!update) {
            // 单点补偿策略：此处只抛异常，不在这里回补 Redis。
            // Redis 回补统一由死信补偿 compensateStock() 执行，避免重复回补。
            throw new BusinessException("扣减秒杀库存失败或库存不足");
        }
        return Result.success();
    }

    /**
     * 死信补偿：回补 Redis 预扣库存并释放限购占位。
     *
     * Key 说明：
     * 1) compensateKey = online:store:seckill:compensate:{messageId}
     *    - 作用：补偿幂等锁，同一条死信消息只补偿一次。
     * 2) stockKey = online:store:seckill:num:{activityId}:{productId}:{specId}
     *    - 作用：秒杀 Redis 库存 key，补偿时执行 INCR 回加库存。
     * 3) buyKey = online:store:seckill:buy_count:{activityId}:{productId}:{specId}:{userId}
     *    - 作用：用户限购占位 key，补偿时删除，允许用户重新下单。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> compensateStock(SeckillOrderMessage message) {
        if (message == null || message.getMessageId() == null || message.getMessageId().isBlank()) {
            throw new BusinessException("死信补偿参数不完整");
        }
        if (message.getUserId() == null || message.getActivityId() == null || CollectionUtil.isEmpty(message.getItems())) {
            throw new BusinessException("死信补偿消息缺少核心字段");
        }
        SeckillOrderMessage.OrderItem item = message.getItems().get(0);
        if (item == null || item.getProductId() == null || item.getSpecId() == null) {
            throw new BusinessException("死信补偿商品字段不完整");
        }

        Integer num = item.getNum();
        if (num == null || num <= 0) {
            num = 1;
        }

        // 幂等锁：同 messageId 只允许补偿一次，防止死信重复投递导致“多补库存”
        String compensateKey = SeckillRedisKeyConstants.SECKILL_COMPENSATE_KEY_PREFIX + message.getMessageId();
        Boolean firstCompensate = stringRedisTemplate.opsForValue()
                .setIfAbsent(compensateKey, "1", 24, TimeUnit.HOURS);
        if (Boolean.FALSE.equals(firstCompensate)) {
            return Result.success();
        }

        // 秒杀 Redis 库存 key：online:store:seckill:num:{activityId}:{productId}:{specId}
        String stockKey = SeckillRedisKeyConstants.SECKILL_NUM_KEY_PREFIX
                + message.getActivityId() + ":" + item.getProductId() + ":" + item.getSpecId();
        // 限购占位 key：online:store:seckill:buy_count:{activityId}:{productId}:{specId}:{userId}
        String buyKey = SeckillRedisKeyConstants.SECKILL_BUY_COUNT_KEY_PREFIX
                + message.getActivityId() + ":" + item.getProductId() + ":" + item.getSpecId() + ":" + message.getUserId();

        try {
            // 回补库存并释放限购占位
            stringRedisTemplate.opsForValue().increment(stockKey, num);
            stringRedisTemplate.delete(buyKey);
            return Result.success();
        } catch (Exception e) {
            // 补偿失败时释放幂等占位，允许后续重试
            stringRedisTemplate.delete(compensateKey);
            throw new BusinessException("死信补偿执行失败: " + e.getMessage());
        }
    }

    /**
     * 计算缓存的过期时间
     * @param activityId
     * @return
     */
    private long resolveCacheTtlSeconds(Long activityId) {
        final long fallbackTtlSeconds = TimeUnit.HOURS.toSeconds(1);
        if (activityId == null) {
            return fallbackTtlSeconds;
        }

        Activity activity = activityService.lambdaQuery()
                .eq(Activity::getId, activityId)
                .one();
        if (activity == null || activity.getEndTime() == null) {
            return fallbackTtlSeconds;
        }

        long remainSeconds = Duration.between(LocalDateTime.now(), activity.getEndTime()).getSeconds();
        return Math.max(remainSeconds, 60L);
    }


}
