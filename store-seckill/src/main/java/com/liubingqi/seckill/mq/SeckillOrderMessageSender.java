package com.liubingqi.seckill.mq;

import com.liubingqi.common.constants.MqConstants;
import com.liubingqi.common.domain.mq.SeckillOrderMessage;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 秒杀下单消息发送器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SeckillOrderMessageSender {

    /**
     * confirm 等待超时时间（秒）
     */
    private static final long CONFIRM_TIMEOUT_SECONDS = 3L;

    private final RabbitTemplate rabbitTemplate;
    /**
     * return 回调暂存区：
     * key = messageId, value = 回调错误信息
     */
    private final Map<String, String> returnReasonMap = new ConcurrentHashMap<>();

    /**
     * 初始化 Return 回调：
     * 当消息到达 exchange 但路由不到任何 queue 时触发。
     */
    @PostConstruct
    public void initCallbacks() {
        rabbitTemplate.setReturnsCallback(returned -> {
            Message msg = returned.getMessage();
            String messageId = msg == null ? null : msg.getMessageProperties().getMessageId();
            String reason = "replyCode=" + returned.getReplyCode()
                    + ", replyText=" + returned.getReplyText()
                    + ", exchange=" + returned.getExchange()
                    + ", routingKey=" + returned.getRoutingKey();
            if (messageId != null && !messageId.isBlank()) {
                returnReasonMap.put(messageId, reason);
            }
            log.warn("MQ return callback, messageId={}, reason={}", messageId, reason);
        });
    }

    /**
     * 发送秒杀下单消息，并基于 confirm/return 判定结果。
     *
     * 判定规则：
     * 1) confirm ack = true 且没有 return 回调，才视为发送成功；
     * 2) confirm nack / confirm 超时 / return 回调触发，均视为失败。
     */
    public SendResult sendCreateOrderMessageWithConfirm(SeckillOrderMessage message) {
        String messageId = message == null ? null : message.getMessageId();
        if (messageId == null || messageId.isBlank()) {
            return SendResult.fail("messageId 为空");
        }

        // 清理同 messageId 的旧 return 信息，避免误判
        returnReasonMap.remove(messageId);
        CorrelationData correlationData = new CorrelationData(messageId);

        rabbitTemplate.convertAndSend(
                MqConstants.SECKILL_ORDER_EXCHANGE,
                MqConstants.SECKILL_ORDER_CREATE_ROUTING_KEY,
                message,
                msg -> {
                    // 把 messageId 写入消息属性，供 return 回调识别对应消息
                    msg.getMessageProperties().setMessageId(messageId);
                    return msg;
                },
                correlationData
        );

        try {
            CorrelationData.Confirm confirm = correlationData.getFuture()
                    .get(CONFIRM_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            boolean ack = confirm != null && confirm.isAck();
            String confirmReason = confirm == null ? "confirm 结果为空" : confirm.getReason();

            // 如果触发了 return，说明路由失败，优先判定失败
            String returnReason = returnReasonMap.remove(messageId);
            if (returnReason != null) {
                return SendResult.fail("return 失败: " + returnReason);
            }

            if (!ack) {
                return SendResult.fail("confirm nack: " + (confirmReason == null ? "unknown" : confirmReason));
            }

            log.info("发送秒杀下单消息成功, messageId={}, userId={}, activityId={}, addressId={}, itemsSize={}",
                    message.getMessageId(),
                    message.getUserId(),
                    message.getActivityId(),
                    message.getAddressId(),
                    message.getItems() == null ? 0 : message.getItems().size());
            return SendResult.ok();
        } catch (Exception e) {
            returnReasonMap.remove(messageId);
            return SendResult.fail("confirm 等待异常: " + e.getMessage());
        }
    }

    /**
     * 发送结果对象：success=true 表示可标记 SENT。
     */
    public record SendResult(boolean success, String reason) {
        public static SendResult ok() {
            return new SendResult(true, null);
        }

        public static SendResult fail(String reason) {
            return new SendResult(false, reason);
        }
    }
}
