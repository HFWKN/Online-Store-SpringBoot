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
     * 秒杀商品详情（规格库存）缓存 Key 前缀
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
     * 实际使用: SECKILL_DUPLICATE_ORDER_KEY_PREFIX + activityId + ":" + productId + ":" + specId + ":" + userId
     */
    public static final String SECKILL_DUPLICATE_ORDER_KEY_PREFIX = "online:store:seckill:duplicate:";

    /**
     * 秒杀用户已购次数 Key 前缀（限购）
     * 实际使用: SECKILL_BUY_COUNT_KEY_PREFIX + activityId + ":" + productId + ":" + specId + ":" + userId
     */
    public static final String SECKILL_BUY_COUNT_KEY_PREFIX = "online:store:seckill:buy_count:";

    /**
     * 秒杀死信补偿幂等 Key 前缀
     * 实际使用: SECKILL_COMPENSATE_KEY_PREFIX + messageId
     */
    public static final String SECKILL_COMPENSATE_KEY_PREFIX = "online:store:seckill:compensate:";


    ///  ---------------分布式锁Key------------------

    /**
     *  用在定时预热任务入口，保证同一时刻只有一个实例在跑预热任务（防止多节点重复预热）
     */
    public static final String SECKILL_WARMUP_TASK_LOCK_KEY = "online:store:seckill:lock:warmup:task";

    /**
     *  用在单个“活动+商品”的库存预热上，作用是避免同一个商品库存被并发重复预热。
     */
    public static final String SECKILL_WARMUP_STOCK_LOCK_KEY_PREFIX = "online:store:seckill:lock:warmup:stock:";

    /**
     *  用在商品规格缓存回填上，作用是防缓存击穿：缓存 miss 时只让一个线程查库+回填，其他线程等待/兜底。
     */
    public static final String SECKILL_SPEC_LOCK_KEY_PREFIX = "online:store:seckill:lock:spec:";

    /**
     * Outbox 派发任务锁 Key，保证同一时刻只由一个实例执行派发任务。
     */
    public static final String SECKILL_OUTBOX_DISPATCH_LOCK_KEY = "online:store:seckill:lock:outbox:dispatch";

    private SeckillRedisKeyConstants() {
    }
}
