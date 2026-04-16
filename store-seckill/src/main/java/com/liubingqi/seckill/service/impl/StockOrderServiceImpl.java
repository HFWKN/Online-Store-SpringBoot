package com.liubingqi.seckill.service.impl;

import com.liubingqi.common.domain.Result;
import com.liubingqi.common.domain.mq.SeckillOrderMessage;
import com.liubingqi.common.utils.UserContext;
import com.liubingqi.seckill.constants.Result_Code;
import com.liubingqi.seckill.constants.SeckillRedisKeyConstants;
import com.liubingqi.seckill.domain.dto.SeckillCreateOrderDto;
import com.liubingqi.seckill.domain.dto.StockDto;
import com.liubingqi.seckill.domain.po.Activity;
import com.liubingqi.seckill.domain.vo.CodeInfoVo;
import com.liubingqi.seckill.mq.SeckillOrderMessageSender;
import com.liubingqi.seckill.service.IActivityService;
import com.liubingqi.seckill.service.IStockOrderService;
import com.liubingqi.seckill.utils.CodeInfoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class StockOrderServiceImpl implements IStockOrderService {

    private static final long TOKEN_TTL_SECONDS = 15L;
    private final StringRedisTemplate stringRedisTemplate;
    private final IActivityService activityService;
    private final SeckillOrderMessageSender mqSender;

    /**
     * 扣减库存-redis-mq通知订单服务生成订单
     *
     *  如果返回FAIL，则需要前端重新调用 gettoken 方法，获取放重复token
     *
     * @param dto
     * @return
     */
    @Override
    public CodeInfoVo stockOrder(SeckillCreateOrderDto dto, String orderToken) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return CodeInfoUtils.of(Result_Code.USER_NOT_LOGIN);// 用户未登录
        }
        // 实际使用: SECKILL_DUPLICATE_ORDER_KEY_PREFIX + activityId + ":" + productId + ":" + specId + ":" + userId
        String key1 = SeckillRedisKeyConstants.SECKILL_DUPLICATE_ORDER_KEY_PREFIX
                + dto.getActivityId() + ":" + dto.getProductId() + ":" + dto.getSpecId() + ":" + userId;
        String token = stringRedisTemplate.opsForValue().get(key1);
        // 判断获取的token是否为空
        if (token == null) {
            return CodeInfoUtils.of(Result_Code.TOKEN_NULL);// token为空
        }
        // 判断token是否一致
        if (!token.equals(orderToken)) {
            return CodeInfoUtils.of(Result_Code.TOKEN_INCONSISTENT);// token不一致
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
            return CodeInfoUtils.of(Result_Code.ACTIVITY_END);// 活动已结束
        }

        Boolean b = stringRedisTemplate.opsForValue().setIfAbsent(buyKey, "lbqsayhello", ttlSeconds, TimeUnit.SECONDS);
        // 判断是否有这个key
        if (Boolean.FALSE.equals(b)) {
            return CodeInfoUtils.of(Result_Code.REPEAT_PURCHASES);// 已购买
        }
        // 用户未购买，执行下单操作
        // 获取要扣减库存的key
        // SECKILL_NUM_KEY_PREFIX + activityId:productId:specId
        String key = SeckillRedisKeyConstants
                .SECKILL_NUM_KEY_PREFIX + dto.getActivityId() + ":" + dto.getProductId() + ":" + dto.getSpecId();

        long remainStock;

        try {
            // 扣减库存-decrement是原子性操作
            remainStock = stringRedisTemplate.opsForValue().decrement(key);
        }catch (Exception e){
            // 扣库存异常时回滚限购占位，允许用户重试
            stringRedisTemplate.delete(buyKey);
            return CodeInfoUtils.of(Result_Code.FAIL);// 下单失败，重新点击购买试试呢^_^。前端需要重新获取token
        }
        if (remainStock >= 0) {
            // 2. 扣减成功
            // 异步发送消息给MQ
            SeckillOrderMessage message = new SeckillOrderMessage();
            message.setMessageId(UUID.randomUUID().toString());// 消息唯一标识
            message.setUserId(UserContext.getUserId());// 用户 ID
            message.setActivityId(dto.getActivityId());// 活动ID
            message.setAddressId(dto.getAddressId());// 地址ID
            message.setPayAmount(dto.getPayAmount());// 付款金额
            message.setRemark("秒杀商品暂不支持备注哦^_^"); //备注
            message.setTimestamp(System.currentTimeMillis());
            // 创建商品明细
            SeckillOrderMessage.OrderItem item = new SeckillOrderMessage.OrderItem();
            item.setProductId(dto.getProductId());// 商品 ID
            item.setSpecId(dto.getSpecId());// 规格 ID
            item.setNum(1); // 购买数量
            // 添加商品明细
            message.setItems(List.of(item));
        try {
            // 发送消息
            mqSender.sendCreateOrderMessage(message);
        }catch (Exception e){
            // 如果发送消息失败，把库存加回去
            stringRedisTemplate.opsForValue().increment(key);
            // 发送失败回滚限购占位，允许用户重试
            stringRedisTemplate.delete(buyKey);
            return CodeInfoUtils.of(Result_Code.FAIL);// 下单失败，重新点击购买试试呢^_^ .前端重新获取token
        }
            // 扣减成功，返回成功结果
            return CodeInfoUtils.of(Result_Code.SUCCESS); // 下单成功
        } else {
            // 3. 库存不足，把多扣的那 1 个补回去
            stringRedisTemplate.opsForValue().increment(key);
            // 下单失败回滚限购占位，避免误判“已买过”
            stringRedisTemplate.delete(buyKey);

            return CodeInfoUtils.of(Result_Code.STOCK_NULL);// 库存不足
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
