package com.liubingqi.order.mq;

import com.liubingqi.common.constants.MqConstants;
import com.liubingqi.common.domain.mq.SeckillOrderMessage;
import com.liubingqi.order.service.IOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
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

        private static final String MQ_KEY_PREFIX = "STOREMQ:";

        private final StringRedisTemplate redisTemplate;
        private final IOrderService orderService;

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
            // 业务失败释放幂等占位，允许 MQ 重试
            redisTemplate.delete(idempotentKey);
            throw e;
        }
    }
}
