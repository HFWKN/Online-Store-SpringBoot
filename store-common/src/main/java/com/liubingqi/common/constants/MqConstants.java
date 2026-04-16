package com.liubingqi.common.constants;

/**
 * MQ 路由常量（秒杀 -> 订单）
 */
public final class MqConstants {

    private MqConstants() {
    }

    /**
     * 秒杀下单交换机
     */
    public static final String SECKILL_ORDER_EXCHANGE = "online.store.seckill.order.exchange";

    /**
     * 订单服务消费队列
     */
    public static final String SECKILL_ORDER_CREATE_QUEUE = "online.store.order.seckill.create.queue";

    /**
     * 秒杀下单路由 key
     */
    public static final String SECKILL_ORDER_CREATE_ROUTING_KEY = "seckill.order.create";

    /**
     * 秒杀订单死信交换机
     */
    public static final String SECKILL_ORDER_DEAD_EXCHANGE = "online.store.order.seckill.dead.exchange";

    /**
     * 秒杀订单死信队列
     */
    public static final String SECKILL_ORDER_DEAD_QUEUE = "online.store.order.seckill.dead.queue";

    /**
     * 秒杀订单死信路由 key
     */
    public static final String SECKILL_ORDER_DEAD_ROUTING_KEY = "seckill.order.dead";
}
