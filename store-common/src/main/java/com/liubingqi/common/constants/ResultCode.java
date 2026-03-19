package com.liubingqi.common.constants;

/**
 * 响应状态码常量
 */
public class ResultCode {

    // 成功
    public static final int SUCCESS = 200;

    // 客户端错误
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int METHOD_NOT_ALLOWED = 405;
    public static final int CONFLICT = 409;
    public static final int UNPROCESSABLE_ENTITY = 422;
    public static final int TOO_MANY_REQUESTS = 429;

    // 服务器错误
    public static final int INTERNAL_SERVER_ERROR = 500;
    public static final int BAD_GATEWAY = 502;
    public static final int SERVICE_UNAVAILABLE = 503;
    public static final int GATEWAY_TIMEOUT = 504;

    // 业务错误码 (自定义)
    public static final int USER_NOT_FOUND = 1001;
    public static final int USER_ALREADY_EXISTS = 1002;
    public static final int INVALID_PASSWORD = 1003;
    public static final int TOKEN_EXPIRED = 1004;
    public static final int TOKEN_INVALID = 1005;

    public static final int PRODUCT_NOT_FOUND = 2001;
    public static final int PRODUCT_OUT_OF_STOCK = 2002;

    public static final int ORDER_NOT_FOUND = 3001;
    public static final int ORDER_STATUS_ERROR = 3002;

    public static final int CART_ITEM_NOT_FOUND = 4001;
    public static final int CART_ITEM_LIMIT_EXCEEDED = 4002;

    public static final int STOCK_INSUFFICIENT = 5001;
    public static final int SECKILL_NOT_STARTED = 5002;
    public static final int SECKILL_ENDED = 5003;
    public static final int SECKILL_LIMIT_EXCEEDED = 5004;
}