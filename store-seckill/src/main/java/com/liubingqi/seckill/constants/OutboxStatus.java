package com.liubingqi.seckill.constants;

/**
 * Outbox 消息状态常量
 */
public final class OutboxStatus {

    private OutboxStatus() {
    }

    /**
     * 待发送
     */
    public static final String NEW = "NEW";

    /**
     * 已发送成功
     */
    public static final String SENT = "SENT";

    /**
     * 发送失败，待重试
     */
    public static final String RETRY = "RETRY";

    /**
     * 达到最大重试次数，进入人工/补偿处理
     */
    public static final String DEAD = "DEAD";
}
