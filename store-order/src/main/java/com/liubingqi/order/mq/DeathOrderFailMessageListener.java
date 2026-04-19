package com.liubingqi.order.mq;

import com.liubingqi.common.constants.MqConstants;
import com.liubingqi.common.domain.mq.SeckillOrderMessage;
import com.liubingqi.common.feignClient.seckill.SeckillFeignClient;
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
    private final SeckillFeignClient seckillFeignClient;

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

        try {
            // 先进行库存补偿与限购占位释放
            seckillFeignClient.compensateStock(message);
        } catch (Exception e) {
            log.error("死信补偿调用失败, messageId={}", message.getMessageId(), e);
            //throw e;
        }
        /**
         *  上面不能抛异常，否则下面不会继续执行，导致失败消息没有入库
         */
        // 把失败信息写入库
        try {
            messageService.saveMessage(message);
        } catch (Exception e) {
            // 通知写库失败不再抛异常，避免同一条死信被无限重投导致日志刷屏。
            log.error("发送失败通知异常，已跳过重试, messageId={}", message.getMessageId(), e);
        }
    }
}
