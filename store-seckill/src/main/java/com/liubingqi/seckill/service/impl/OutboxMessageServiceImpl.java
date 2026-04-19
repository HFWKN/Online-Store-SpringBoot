package com.liubingqi.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.liubingqi.seckill.domain.po.OutboxMessage;
import com.liubingqi.seckill.constants.OutboxStatus;
import com.liubingqi.seckill.mapper.OutboxMessageMapper;
import com.liubingqi.seckill.service.IOutboxMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 秒杀Outbox本地消息表 服务实现类
 * </p>
 *
 * @author lbq
 * @since 2026-04-16
 */
@Service
public class OutboxMessageServiceImpl extends ServiceImpl<OutboxMessageMapper, OutboxMessage> implements IOutboxMessageService {

    /**
     *  查询待派发消息
     * @param now
     * @param limit
     * @return
     */
    @Override
    public List<OutboxMessage> queryPendingMessages(LocalDateTime now, int limit) {
        LambdaQueryWrapper<OutboxMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(OutboxMessage::getStatus, OutboxStatus.NEW, OutboxStatus.RETRY)
                .and(w -> w
                        .isNull(OutboxMessage::getNextRetryTime)
                        .or()
                        .le(OutboxMessage::getNextRetryTime, now))
                .orderByAsc(OutboxMessage::getId)
                .last("limit " + limit);
        return list(wrapper);
    }

    /**
     * 标记消息发送成功
     */
    @Override
    public void markSent(Long id) {
        OutboxMessage update = new OutboxMessage();
        update.setId(id);
        update.setStatus(OutboxStatus.SENT);
        update.setUpdateTime(LocalDateTime.now());
        updateById(update);
    }

    /**
     * 标记消息重试
     */
    @Override
    public void markRetry(Long id, int retryCount, LocalDateTime nextRetryTime, String lastError) {
        OutboxMessage update = new OutboxMessage();
        update.setId(id);
        update.setStatus(OutboxStatus.RETRY);
        update.setRetryCount(retryCount);
        update.setNextRetryTime(nextRetryTime);
        update.setLastError(lastError);
        update.setUpdateTime(LocalDateTime.now());
        updateById(update);
    }

    /**
     * 标记消息死亡
     */
    @Override
    public void markDead(Long id, int retryCount, String lastError) {
        OutboxMessage update = new OutboxMessage();
        update.setId(id);
        update.setStatus(OutboxStatus.DEAD);
        update.setRetryCount(retryCount);
        update.setLastError(lastError);
        update.setUpdateTime(LocalDateTime.now());
        updateById(update);
    }
}
