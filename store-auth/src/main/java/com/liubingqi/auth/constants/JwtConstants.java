package com.liubingqi.auth.constants;

/**
 * JWT相关常量
 */
public class JwtConstants {

    /**
     * JWT密钥
     */
    public static final String SECRET = "online-store-secret-key-2024-liubingqi";

    /**
     * JWT过期时间（秒）- 24小时
     */
    public static final Long EXPIRE = 24 * 60 * 60L;

    /**
     * JWT签发者
     */
    public static final String ISSUER = "online-store";

    /**
     * JWT用户名声明键
     */
    public static final String USERNAME_CLAIM = "username";

    /**
     * Authorization请求头名称
     */
    public static final String AUTHORIZATION_HEADER = "Authorization";

    /**
     * Bearer前缀
     */
    public static final String BEARER_PREFIX = "Bearer ";

    /**
     * 用户信息请求头（传递给下游服务）
     */
    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String USERNAME_HEADER = "X-Username";

}