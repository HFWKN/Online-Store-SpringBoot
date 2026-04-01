package com.liubingqi.order.constants;

import org.springframework.data.redis.core.script.DefaultRedisScript;

public class OrderLuaScriptConstants {

    /**
     * 原子校验并删除下单 token 的 Lua 脚本
     * 返回值：1-校验成功且已删除，0-校验失败或已失效
     */
    public static final DefaultRedisScript<Long> CHECK_AND_DELETE_ORDER_TOKEN_SCRIPT;

    static {
        CHECK_AND_DELETE_ORDER_TOKEN_SCRIPT = new DefaultRedisScript<>();
        CHECK_AND_DELETE_ORDER_TOKEN_SCRIPT.setScriptText(
                "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                        "return redis.call('del', KEYS[1]) " +
                        "else " +
                        "return 0 " +
                        "end"
        );
        CHECK_AND_DELETE_ORDER_TOKEN_SCRIPT.setResultType(Long.class);
    }

    private OrderLuaScriptConstants() {
    }
}
