package com.liubingqi.auth.utils;

import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import com.liubingqi.auth.constants.JwtConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * JWT工具类
 * 基于Hutool实现JWT令牌的生成和校验
 */
@Slf4j
@Component
public class JwtTool {

    /**
     * 生成JWT令牌
     *
     * @param userId   用户ID
     * @param username 用户名
     * @return JWT令牌
     */
    public String generateToken(Long userId, String username) {
        try {
            JWTSigner jwtSigner = createSigner();
            return JWT.create()
                    .setPayload("userId", userId)
                    .setPayload(JwtConstants.USERNAME_CLAIM, username)
                    .setIssuer(JwtConstants.ISSUER)
                    .setIssuedAt(new Date())
                    .setExpiresAt(new Date(System.currentTimeMillis() + JwtConstants.EXPIRE * 1000))
                    .setSigner(jwtSigner)
                    .sign();
        } catch (Exception e) {
            log.error("生成JWT令牌失败，用户ID: {}, 用户名: {}", userId, username, e);
            throw new RuntimeException("生成JWT令牌失败", e);
        }
    }

    /**
     * 解析JWT令牌
     *
     * @param token JWT令牌
     * @return JWT对象
     */
    public JWT parseToken(String token) {
        try {
            JWTSigner jwtSigner = createSigner();
            // 解析token
            JWT jwt = JWT.of(token).setSigner(jwtSigner);
            
            // 验证签名
            if (!jwt.verify()) {
                throw new RuntimeException("JWT令牌签名验证失败");
            }
            
            // 验证是否过期
            try {
                JWTValidator.of(jwt).validateDate();
            } catch (ValidateException e) {
                throw new RuntimeException("JWT令牌已过期");
            }
            
            return jwt;
        } catch (Exception e) {
            log.error("解析JWT令牌失败: {}", e.getMessage());
            throw new RuntimeException("解析JWT令牌失败: " + e.getMessage(), e);
        }
    }

    /**
     * 校验JWT令牌有效性
     *
     * @param token JWT令牌
     * @return true-有效，false-无效
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            log.debug("JWT令牌校验失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从JWT令牌中获取用户ID
     *
     * @param token JWT令牌
     * @return 用户ID
     */
    public Long getUserId(String token) {
        JWT jwt = parseToken(token);
        Object userId = jwt.getPayload("userId");
        if (userId == null) {
            throw new RuntimeException("JWT令牌中不包含用户ID");
        }
        return Long.valueOf(userId.toString());
    }

    /**
     * 从JWT令牌中获取用户名
     *
     * @param token JWT令牌
     * @return 用户名
     */
    public String getUsername(String token) {
        JWT jwt = parseToken(token);
        Object username = jwt.getPayload(JwtConstants.USERNAME_CLAIM);
        if (username == null) {
            throw new RuntimeException("JWT令牌中不包含用户名");
        }
        return username.toString();
    }

    private JWTSigner createSigner() {
        return JWTSignerUtil.hs256(JwtConstants.SECRET.getBytes());
    }
}
