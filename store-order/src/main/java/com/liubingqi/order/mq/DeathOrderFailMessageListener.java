package com.liubingqi.order.mq;

import com.liubingqi.common.constants.MqConstants;
import com.liubingqi.common.domain.mq.SeckillOrderMessage;
import com.liubingqi.order.service.IMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 订单失败消息监听器（监听死信队列）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeathOrderFailMessageListener {

    private final IMessageService messageService;

    /**
     * 监听死信队列
     */
    @RabbitListener(queues = MqConstants.SECKILL_ORDER_DEAD_QUEUE)
    public void onDeadMessage(SeckillOrderMessage message) {
        if (message == null) {
            log.warn("收到死信消息，但消息体为空");
            return;
        }

        log.error("收到死信消息, messageId={}, userId={}, activityId={}, addressId={}, itemsSize={}",
                message.getMessageId(),
                message.getUserId(),
                message.getActivityId(),
                message.getAddressId(),
                message.getItems() == null ? 0 : message.getItems().size());

        // 调用通知前端方法。
        messageService.sendMessage(message);
    }
}
