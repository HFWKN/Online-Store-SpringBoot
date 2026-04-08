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
     * 实际使用: SECKILL_DETAIL_KEY_PREFIX + activityId + ":" + productId
     */
    public static final String SECKILL_DETAIL_KEY_PREFIX = "online:store:seckill:detail:";

    /**
     * 秒杀库存缓存 Key 前缀
     * 实际使用: SECKILL_STOCK_KEY_PREFIX + stockId
     */
    public static final String SECKILL_STOCK_KEY_PREFIX = "online:store:seckill:stock:";

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

    private SeckillRedisKeyConstants() {
    }
}
