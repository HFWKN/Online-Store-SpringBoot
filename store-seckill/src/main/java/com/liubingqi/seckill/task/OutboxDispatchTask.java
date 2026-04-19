package com.liubingqi.seckill.task;

import com.alibaba.fastjson2.JSON;
import com.liubingqi.common.domain.mq.SeckillOrderMessage;
import com.liubingqi.seckill.constants.SeckillRedisKeyConstants;
import com.liubingqi.seckill.domain.po.OutboxMessage;
import com.liubingqi.seckill.mq.SeckillOrderMessageSender;
import com.liubingqi.seckill.service.IOutboxMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Outbox 本地消息派发任务：
 * 1) 周期性扫描 NEW/RETRY 状态的待发送消息；
 * 2) 派发到 MQ；
 * 3) 根据发送结果更新状态（SENT/RETRY/DEAD）。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxDispatchTask {

    //单次扫描待派发消息上限，避免一次处理过多造成任务阻塞。
    private static final int FETCH_LIMIT = 100;
    //最大重试次数，超过后标记为 DEAD。
    private static final int MAX_RETRY_COUNT = 5;
    //异常信息最大保留长度，防止写库字段超长。
    private static final int MAX_ERROR_LENGTH = 450;

    private final IOutboxMessageService outboxMessageService;
    private final SeckillOrderMessageSender mqSender;
    private final RedissonClient redissonClient;

    /**
     * 定时派发入口（默认每 3 秒执行一次，可通过配置覆盖）。
     */
    @Scheduled(fixedDelayString = "${outbox.dispatch.fixed-delay-ms:3000}")//定时任务，每3秒执行一次
    public void dispatch() {
        // 获取锁
        RLock lock = redissonClient.getLock(SeckillRedisKeyConstants.SECKILL_OUTBOX_DISPATCH_LOCK_KEY);
        // 获取锁失败直接跳过本轮
        boolean locked = false;
        try {
            // 单实例执行派发任务：抢不到锁直接跳过本轮，避免多节点重复派发。
            locked = lock.tryLock(0, 30, TimeUnit.SECONDS);
            if (!locked) {
                return;
            }

            LocalDateTime now = LocalDateTime.now();
            // 只拉取“现在应该发送”的消息，单次最多 FETCH_LIMIT 条，避免一次处理过多。
            List<OutboxMessage> pendingList = outboxMessageService.queryPendingMessages(now, FETCH_LIMIT);
            if (pendingList == null || pendingList.isEmpty()) {
                return;
            }

            for (OutboxMessage outbox : pendingList) {
                // 脏数据保护：缺主键/缺消息体的记录直接跳过，避免阻塞任务。
                if (outbox == null || outbox.getId() == null || outbox.getPayloadJson() == null || outbox.getPayloadJson().isBlank()) {
                    continue;
                }
                try {
                    // 把本地 JSON 还原成业务消息对象，准备发送到订单服务。
                    SeckillOrderMessage message = JSON.parseObject(outbox.getPayloadJson(), SeckillOrderMessage.class);
                    if (message == null || message.getMessageId() == null) {
                        throw new IllegalStateException("payload 无法反序列化为有效消息");
                    }
                    // 向mq发送信息
                    // V2：向mq发送信息后必须以 confirm/return 结果判定是否成功，不能仅靠“未抛异常”。
                    SeckillOrderMessageSender.SendResult sendResult = mqSender.sendCreateOrderMessageWithConfirm(message);
                    if (!sendResult.success()) {
                        // 如果发送失败，则抛出异常，进入 catch 逻辑处理。
                        throw new IllegalStateException(sendResult.reason());
                    }
                    // confirm=ack 且无 return：标记 SENT(成功)，后续不再重试。
                    outboxMessageService.markSent(outbox.getId());
                } catch (Exception e) {
                    // 发送失败：累加重试次数，根据次数决定 RETRY 还是 DEAD。
                    int currentRetry = outbox.getRetryCount() == null ? 0 : outbox.getRetryCount();
                    int nextRetryCount = currentRetry + 1;
                    String error = safeError(e);

                    if (nextRetryCount >= MAX_RETRY_COUNT) {
                        // 标记死亡
                        outboxMessageService.markDead(outbox.getId(), nextRetryCount, error);
                        log.error("Outbox 消息达到最大重试次数，标记 DEAD, id={}, messageId={}", outbox.getId(), outbox.getMessageId(), e);
                    } else {
                        // 退避重试：2^retryCount 秒，最大 60 秒，避免瞬时故障时重试风暴。
                        int delaySeconds = Math.min(1 << Math.min(nextRetryCount, 5), 60);
                        LocalDateTime nextRetryTime = LocalDateTime.now().plusSeconds(delaySeconds);
                        // 标记重试
                        outboxMessageService.markRetry(outbox.getId(), nextRetryCount, nextRetryTime, error);
                        log.warn("Outbox 派发失败，准备重试, id={}, messageId={}, retryCount={}",
                                outbox.getId(), outbox.getMessageId(), nextRetryCount, e);
                    }
                }
            }
        } catch (InterruptedException e) {
            // 抢锁等待过程中线程被中断，结束本轮任务。
            Thread.currentThread().interrupt();
            log.warn("Outbox 派发任务抢锁被中断");
        } finally {
            // 仅在当前线程持有锁时释放
            if (locked && lock.isHeldByCurrentThread()) {
                // 释放锁
                lock.unlock();
            }
        }
    }

    /**
     * 截断异常信息，避免写库时超长。
     */
    private String safeError(Exception e) {
        String msg = e == null ? "unknown error" : e.getMessage();
        if (msg == null || msg.isBlank()) {
            msg = e == null ? "unknown error" : e.getClass().getSimpleName();
        }
        return msg.length() > MAX_ERROR_LENGTH ? msg.substring(0, MAX_ERROR_LENGTH) : msg;
    }
}
