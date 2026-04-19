package com.liubingqi.seckill.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 秒杀Outbox本地消息表
 * </p>
 *
 * @author lbq
 * @since 2026-04-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("seckill_outbox_message")
public class OutboxMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 业务消息ID（与MQ消息messageId一致）
     */
    private String messageId;

    /**
     * 消息体JSON（SeckillOrderMessage序列化）
     */
    private String payloadJson;

    /**
     * 消息状态：NEW/RETRY/SENT/DEAD
     */
    private String status;

    /**
     * 已重试次数
     */
    private Integer retryCount;

    /**
     * 下次重试时间
     */
    private LocalDateTime nextRetryTime;

    /**
     * 最近一次失败原因
     */
    private String lastError;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
