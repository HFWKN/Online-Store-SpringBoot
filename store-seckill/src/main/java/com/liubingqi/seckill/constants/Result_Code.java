package com.liubingqi.seckill.constants;

import lombok.Getter;

/**
 *  状态码枚举类
 */
@Getter
public enum Result_Code {
    SUCCESS(200, "已受理，稍等片刻进入订单界面查看"),
    FAIL(500, "下单失败，重新点击购买试试呢^_^"),
    REPEAT_PURCHASES(401, "您已经购买过了哦-_-"),
    TOKEN_NULL(404, "token为空"),
    TOKEN_INCONSISTENT(402, "token不一致"),
    ACTIVITY_END(408, "活动已结束"),
    STOCK_NULL(403, "库存不足"),
    USER_NOT_LOGIN(405, "用户未登录");
  /*  USER_LOGOUT_SUCCESS(406, "用户登出成功"),
    USER_LOGIN_SUCCESS(407, "用户登录成功");*/

    private final Integer code;
    private final String message;

    Result_Code(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
