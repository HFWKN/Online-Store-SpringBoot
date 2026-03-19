package com.liubingqi.common.utils;

/**
 * 用户上下文工具类
 * 基于ThreadLocal存储当前登录用户信息
 */
public class UserContext {

    private static final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();
    private static final ThreadLocal<String> usernameHolder = new ThreadLocal<>();

    /**
     * 设置用户ID
     */
    public static void setUserId(Long userId) {
        userIdHolder.set(userId);
    }

    /**
     * 获取用户ID
     */
    public static Long getUserId() {
        return userIdHolder.get();
    }

    /**
     * 设置用户名
     */
    public static void setUsername(String username) {
        usernameHolder.set(username);
    }

    /**
     * 获取用户名
     */
    public static String getUsername() {
        return usernameHolder.get();
    }

    /**
     * 清除用户信息（防止内存泄漏）
     */
    public static void clear() {
        userIdHolder.remove();
        usernameHolder.remove();
    }
}
