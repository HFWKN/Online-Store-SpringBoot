package com.liubingqi.order.constants;

public class OrderRedisKeyConstants {

    /**
     * 下单幂等 token 的 Redis Key 前缀
     * 实际使用时按：ORDER_TOKEN_KEY_PREFIX + userId 进行拼接
     */
    public static final String ORDER_TOKEN_KEY_PREFIX = "online:store:pay:order_token:";

    private OrderRedisKeyConstants() {
    }
}
