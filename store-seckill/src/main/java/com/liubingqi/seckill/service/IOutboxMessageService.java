package com.liubingqi.seckill.service;

import com.liubingqi.seckill.domain.po.OutboxMessage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 秒杀Outbox本地消息表 服务类
 * </p>
 *
 * @author lbq
 * @since 2026-04-16
 */
public interface IOutboxMessageService extends IService<OutboxMessage> {

    /**
     * 查询待派发消息（NEW + RETRY 且到达重试时间）
     */
    List<OutboxMessage> queryPendingMessages(LocalDateTime now, int limit);

    /**
     * 标记消息发送成功
     */
    void markSent(Long id);

    /**
     * 标记消息重试
     */
    void markRetry(Long id, int retryCount, LocalDateTime nextRetryTime, String lastError);

    /**
     * 标记消息死亡
     */
    void markDead(Long id, int retryCount, String lastError);
}
