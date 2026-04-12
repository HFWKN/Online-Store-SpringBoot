package com.liubingqi.seckill.service.impl;

import com.liubingqi.common.domain.Result;
import com.liubingqi.common.utils.UserContext;
import com.liubingqi.seckill.constants.SeckillRedisKeyConstants;
import com.liubingqi.seckill.domain.dto.SeckillCreateOrderDto;
import com.liubingqi.seckill.domain.dto.StockDto;
import com.liubingqi.seckill.domain.po.Activity;
import com.liubingqi.seckill.service.IActivityService;
import com.liubingqi.seckill.service.IStockOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class StockOrderServiceImpl implements IStockOrderService {

    private static final long TOKEN_TTL_SECONDS = 15L;
    private final StringRedisTemplate stringRedisTemplate;
    private final IActivityService activityService;

    /**
     * 扣减库存-redis-mq通知订单服务生成订单
     *
     * @param dto
     * @return
     */
    @Override
    public String stockOrder(SeckillCreateOrderDto dto, String orderToken) {
        Long userId = UserContext.getUserId();
        // 实际使用: SECKILL_DUPLICATE_ORDER_KEY_PREFIX + activityId + ":" + productId + ":" + specId + ":" + userId
        String key1 = SeckillRedisKeyConstants.SECKILL_DUPLICATE_ORDER_KEY_PREFIX
                + dto.getActivityId() + ":" + dto.getProductId() + ":" + dto.getSpecId() + ":" + userId;
        String token = stringRedisTemplate.opsForValue().get(key1);
        // 判断获取的token是否为空
        if (token == null) {
            return "token为空";
        }
        // 判断token是否一致
        if (!token.equals(orderToken)) {
            return "token不一致";
        }
        // token校验通过后立即删除，避免重复使用
        stringRedisTemplate.delete(key1);
        // token一致，下一步操作
        // 判断限购
        Long specId = dto.getSpecId();
        Long productId = dto.getProductId();
        Long activityId = dto.getActivityId();

        String buyKey = SeckillRedisKeyConstants.SECKILL_BUY_COUNT_KEY_PREFIX
                + activityId + ":" + productId + ":" + specId + ":" + userId;
        // 获取当前活动过期时间
        Long ttlSeconds = getActivityTime(activityId);
        if (ttlSeconds == null || ttlSeconds <= 0){
            return "活动已结束";
        }

        Boolean b = stringRedisTemplate.opsForValue().setIfAbsent(buyKey, "lbqsayhello", ttlSeconds, TimeUnit.SECONDS);
        // 判断是否有这个key
        if (Boolean.FALSE.equals(b)) {
            return "限购一件哦 -_-";
        }
        // 用户未购买，执行下单操作

        // 获取要扣减库存的key
        // SECKILL_NUM_KEY_PREFIX + activityId:productId:specId
        String key = SeckillRedisKeyConstants
                .SECKILL_NUM_KEY_PREFIX + dto.getActivityId() + ":" + dto.getProductId() + ":" + dto.getSpecId();

        // 扣减库存-decrement是原子性操作
        Long remainStock = stringRedisTemplate.opsForValue().decrement(key);

        if (remainStock >= 0) {
            // 2. 扣减成功
            // 异步发送消息给MQ
            return "下单成功";
        } else {
            // 3. 库存不足，把多扣的那 1 个补回去
            stringRedisTemplate.opsForValue().increment(key);
            // 下单失败回滚限购占位，避免误判“已买过”
            stringRedisTemplate.delete(buyKey);

            return "库存不足";

        }
    }


    /**
     *  生成用户下单token---防重复下单
     * @return
     */
    @Override
    public Result<String> gettoken(StockDto dto) {
        // 获取当前用户信息
        Long userId = UserContext.getUserId();
        // 生成token
        // 获取当前时间戳
        long timeMillis = System.currentTimeMillis();
        // 把用户id放在时间戳后面
        String token = timeMillis + ":" + userId;
        // 存入redis中
        // 实际使用: SECKILL_DUPLICATE_ORDER_KEY_PREFIX + activityId + ":" + productId + ":" + specId + ":" + userId
        String key = SeckillRedisKeyConstants.SECKILL_DUPLICATE_ORDER_KEY_PREFIX
                + dto.getActivityId() + ":" + dto.getProductId() + ":" + dto.getSpecId() + ":" + userId;
        try {
            stringRedisTemplate.opsForValue().set(key, token, TOKEN_TTL_SECONDS, TimeUnit.SECONDS);
        }catch (Exception e){
            return Result.fail("生成用户下单token失败");
        }
            return Result.success(token);
    }


    /**
     *  计算当前活动的剩余时间
     * @param activityId
     * @return
     */
    private Long getActivityTime(Long activityId) {
        Activity activity = activityService.lambdaQuery()
                .eq(Activity::getId, activityId)
                .one();
        if (activity == null || activity.getEndTime() == null) {
            return null;
        }
        // 计算剩余时间（s）
        return Duration.between(LocalDateTime.now(), activity.getEndTime()).getSeconds();
    }
}
