package com.liubingqi.order.mq;

import com.liubingqi.common.constants.MqConstants;
import com.liubingqi.common.domain.mq.SeckillOrderMessage;
import com.liubingqi.order.service.IOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 秒杀下单消息监听器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SeckillOrderMessageListener {

    /**
     * 消费幂等 key 前缀（同一 messageId 只允许成功处理一次）
     * 实际使用: STOREQ_KEY_PREFIX + messageId
     */
    private static final String MQ_KEY_PREFIX = "STOREMQ:";

    /**
     * 消费重试次数控制 key 前缀（当前策略：每条消息最多补发重试一次）
     * 实际使用: RETRY_ONCE_KEY_PREFIX + messageId
     */
    private static final String RETRY_ONCE_KEY_PREFIX = "STOREMQ:RETRY_ONCE:";

    private final StringRedisTemplate redisTemplate;
    private final IOrderService orderService;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = MqConstants.SECKILL_ORDER_CREATE_QUEUE)
    public void onSeckillOrderMessage(SeckillOrderMessage message) {
        log.info("收到秒杀下单消息, messageId={}, userId={}, activityId={}, addressId={}, itemsSize={}, timestamp={}",
                message.getMessageId(),
                message.getUserId(),
                message.getActivityId(),
                message.getAddressId(),
                message.getItems() == null ? 0 : message.getItems().size(),
                message.getTimestamp());

        // 按 messageId 判断这条消息是否已经处理过（防重复消费重复下单）
        String idempotentKey = MQ_KEY_PREFIX + message.getMessageId();
        Boolean firstConsume = redisTemplate.opsForValue()
                .setIfAbsent(idempotentKey, "1", 24, TimeUnit.HOURS);
        if (Boolean.FALSE.equals(firstConsume)) {
            log.info("重复消息，已忽略, messageId={}", message.getMessageId());
            return;
        }
        try {
            // 该messageId第一次通知消费者
            orderService.createOrderFromMq(message);
        } catch (Exception e) {
            // 业务失败后只允许重试一次，超过一次则直接进入死信队列
            log.error("秒杀下单失败,重试一次, messageId={}", message.getMessageId(), e);
            String retryOnceKey = RETRY_ONCE_KEY_PREFIX + message.getMessageId();
            Boolean firstRetry = redisTemplate.opsForValue()
                    .setIfAbsent(retryOnceKey, "1", 24, TimeUnit.HOURS);

            // 无论重试还是进入死信，先释放消费幂等占位，避免下一次被误判重复
            redisTemplate.delete(idempotentKey);

            if (Boolean.TRUE.equals(firstRetry)) {
                log.warn("准备进行一次消费重试, messageId={}", message.getMessageId());
                rabbitTemplate.convertAndSend(
                        MqConstants.SECKILL_ORDER_EXCHANGE,
                        MqConstants.SECKILL_ORDER_CREATE_ROUTING_KEY,
                        message
                );
                return;
            }

            throw new AmqpRejectAndDontRequeueException(
                    "消费失败且已重试一次，拒绝重回队列并进入死信队列, messageId=" + message.getMessageId(), e);
        }
    }
}
