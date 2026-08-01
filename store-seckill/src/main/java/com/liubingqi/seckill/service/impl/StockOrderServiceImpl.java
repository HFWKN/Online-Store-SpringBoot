package com.liubingqi.seckill.service.impl;

import com.alibaba.fastjson2.JSON;
import com.liubingqi.common.domain.Result;
import com.liubingqi.common.domain.mq.SeckillOrderMessage;
import com.liubingqi.common.utils.UserContext;
import com.liubingqi.seckill.constants.OutboxStatus;
import com.liubingqi.seckill.constants.Result_Code;
import com.liubingqi.seckill.constants.SeckillRedisKeyConstants;
import com.liubingqi.seckill.domain.dto.SeckillCreateOrderDto;
import com.liubingqi.seckill.domain.dto.StockDto;
import com.liubingqi.seckill.domain.po.Activity;
import com.liubingqi.seckill.domain.po.OutboxMessage;
import com.liubingqi.seckill.domain.vo.CodeInfoVo;
import com.liubingqi.seckill.mq.SeckillOrderMessageSender;
import com.liubingqi.seckill.service.IActivityService;
import com.liubingqi.seckill.service.IOutboxMessageService;
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
    private final IOutboxMessageService outboxService;
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

        // 防重复下单key  //  拼接 Key：包含了活动ID、商品ID、规格ID和用户ID。这说明：这个 Token 是这个用户、针对这个商品的专属凭证。
        // 用户下单的时候，前端会请求后端的getToken方法，生成本次下单的唯一token，getToken方法会将此Token存入redis
        String key1 = SeckillRedisKeyConstants.SECKILL_DUPLICATE_ORDER_KEY_PREFIX
                + dto.getActivityId() + ":" + dto.getProductId() + ":" + dto.getSpecId() + ":" + userId;
        // 如果没有这个Token，就说明本次下单不是正常途径的。
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

        // 这个key是判断该用户是否购买过某个商品的某个规格。限购用
        String buyKey = SeckillRedisKeyConstants.SECKILL_BUY_COUNT_KEY_PREFIX
                + activityId + ":" + productId + ":" + specId + ":" + userId;
        // 获取当前活动过期时间
        Long ttlSeconds = getActivityTime(activityId);
        if (ttlSeconds == null || ttlSeconds <= 0){
            return CodeInfoUtils.of(Result_Code.ACTIVITY_END);// 活动已结束
        }

        // 判断是否有这个key(也就是判断该用户有没有重复购买同一个规格的商品)
        Boolean b = stringRedisTemplate.opsForValue().setIfAbsent(buyKey, "lbqsayhello", ttlSeconds, TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(b)) {
            return CodeInfoUtils.of(Result_Code.REPEAT_PURCHASES);// 已购买
        }
        // 用户未购买，执行下单操作
        // 获取要扣减库存的key
        // SECKILL_NUM_KEY_PREFIX + activityId:productId:specId
        // 该key为某一商品库存Key
        String key = SeckillRedisKeyConstants
                .SECKILL_NUM_KEY_PREFIX + dto.getActivityId() + ":" + dto.getProductId() + ":" + dto.getSpecId();

        long remainStock;

        try {
            // 扣减库存(库存-1)-(decrement)是原子性操作
            remainStock = stringRedisTemplate.opsForValue().decrement(key);
        }catch (Exception e){
            // 扣库存异常时回滚限购占位，允许用户重试(删除该用户对于某一商品的限购)
            stringRedisTemplate.delete(buyKey);
            return CodeInfoUtils.of(Result_Code.FAIL);// 下单失败，重新点击购买试试呢^_^。前端需要重新获取token
        }
        if (remainStock >= 0) {
            // 2. 扣减成功，写入 Outbox 本地消息表，后续由定时任务派发到 MQ
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

            // 创建 OutboxMessage，用于存储消息，待 MQ 消费
            OutboxMessage outbox = new OutboxMessage()
                    .setMessageId(message.getMessageId())
                    .setPayloadJson(JSON.toJSONString(message))
                    .setStatus(OutboxStatus.NEW)
                    .setRetryCount(0)
                    .setCreateTime(LocalDateTime.now())
                    .setUpdateTime(LocalDateTime.now());

            try {
                // 将消息添加到本地信息表(outbox表)
                outboxService.save(outbox);
            } catch (Exception e) {
                // 写 Outbox 失败，回滚 Redis 库存和限购占位，允许用户重试
                stringRedisTemplate.opsForValue().increment(key);
                stringRedisTemplate.delete(buyKey);
                return CodeInfoUtils.of(Result_Code.FAIL);// 下单失败，重新点击购买试试呢^_^
            }
            // redis库存扣减且落库本地消息表成功，返回成功结果
            return CodeInfoUtils.of(Result_Code.SUCCESS); // 已受理，稍后进入订单界面查看订单
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
