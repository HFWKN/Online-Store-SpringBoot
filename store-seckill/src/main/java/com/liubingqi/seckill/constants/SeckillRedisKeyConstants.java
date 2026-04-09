package com.liubingqi.seckill.constants;

/**
 * 秒杀模块 Redis Key 常量
 */
public class SeckillRedisKeyConstants {

    /**
     * 秒杀活动商品列表缓存 Key 前缀
     * 实际使用: SECKILL_LIST_KEY_PREFIX + activityId
     */
    public static final String SECKILL_LIST_KEY_PREFIX = "online:store:seckill:list:";

    /**
     * 秒杀商品详情缓存 Key 前缀
     * 实际使用: SECKILL_SPEC_KEY_PREFIX + activityId + ":" + productId
     */
    public static final String SECKILL_SPEC_KEY_PREFIX = "online:store:seckill:spec:";

    /**
     * 秒杀库存缓存 Key 前缀
     * 实际使用: SECKILL_NUM_KEY_PREFIX + activityId:productId:specId
     */
    public static final String SECKILL_NUM_KEY_PREFIX = "online:store:seckill:num:";

    /**
     * 秒杀令牌缓存 Key 前缀（防刷）
     * 实际使用: SECKILL_TOKEN_KEY_PREFIX + userId + ":" + activityId + ":" + productId
     */
    public static final String SECKILL_TOKEN_KEY_PREFIX = "online:store:seckill:token:";

    /**
     * 秒杀重复下单防重 Key 前缀（幂等）
     * 实际使用: SECKILL_DUPLICATE_ORDER_KEY_PREFIX + userId + ":" + stockId
     */
    public static final String SECKILL_DUPLICATE_ORDER_KEY_PREFIX = "online:store:seckill:duplicate:";

    /**
     * 秒杀用户已购次数 Key 前缀（限购）
     * 实际使用: SECKILL_BUY_COUNT_KEY_PREFIX + userId + ":" + activityId
     */
    public static final String SECKILL_BUY_COUNT_KEY_PREFIX = "online:store:seckill:buy_count:";

    public static final String SECKILL_WARMUP_TASK_LOCK_KEY = "online:store:seckill:lock:warmup:task";

    public static final String SECKILL_WARMUP_STOCK_LOCK_KEY_PREFIX = "online:store:seckill:lock:warmup:stock:";

    public static final String SECKILL_SPEC_LOCK_KEY_PREFIX = "online:store:seckill:lock:spec:";

    private SeckillRedisKeyConstants() {
    }
}
