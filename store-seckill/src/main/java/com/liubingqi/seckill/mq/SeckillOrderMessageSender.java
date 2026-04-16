package com.liubingqi.seckill.mq;

import com.liubingqi.common.constants.MqConstants;
import com.liubingqi.common.domain.mq.SeckillOrderMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * 秒杀下单消息发送器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SeckillOrderMessageSender {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 发送秒杀下单通知给订单服务
     */
    public void sendCreateOrderMessage(SeckillOrderMessage message) {
        rabbitTemplate.convertAndSend(
                MqConstants.SECKILL_ORDER_EXCHANGE,
                MqConstants.SECKILL_ORDER_CREATE_ROUTING_KEY,
                message
        );
        log.info("发送秒杀下单消息成功, messageId={}, userId={}, activityId={}, addressId={}, itemsSize={}",
                message.getMessageId(),
                message.getUserId(),
                message.getActivityId(),
                message.getAddressId(),
                message.getItems() == null ? 0 : message.getItems().size());
    }
}
