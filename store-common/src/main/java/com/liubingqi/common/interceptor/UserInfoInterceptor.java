package com.liubingqi.common.interceptor;

import cn.hutool.core.util.StrUtil;
import com.liubingqi.common.constants.JwtConstants;
import com.liubingqi.common.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 用户信息拦截器
 * 从请求头中获取用户信息并存入UserContext
 * 只在Servlet Web应用中生效，WebFlux应用中不会加载
 */
@Slf4j
@Component
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class UserInfoInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 从请求头获取用户ID
        String userIdStr = request.getHeader(JwtConstants.USER_ID_HEADER);
        if (StrUtil.isNotBlank(userIdStr)) {
            try {
                Long userId = Long.valueOf(userIdStr);
                UserContext.setUserId(userId);
                log.debug("用户ID已存入上下文: {}", userId);
            } catch (NumberFormatException e) {
                log.warn("用户ID格式错误: {}", userIdStr);
            }
        }

        // 从请求头获取用户名
        String username = request.getHeader(JwtConstants.USERNAME_HEADER);
        if (StrUtil.isNotBlank(username)) {
            UserContext.setUsername(username);
            log.debug("用户名已存入上下文: {}", username);
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                                Object handler, Exception ex) {
        // 清除用户信息，防止内存泄漏
        UserContext.clear();
        log.debug("用户上下文已清除");
    }
}
