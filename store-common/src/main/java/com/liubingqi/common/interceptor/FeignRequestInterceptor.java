package com.liubingqi.common.interceptor;

import com.liubingqi.common.constants.JwtConstants;
import com.liubingqi.common.utils.UserContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Feign请求拦截器
 * 在服务间调用时自动传递用户信息
 */
@Slf4j
@Component
public class FeignRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        // 从当前服务的UserContext获取用户信息
        Long userId = UserContext.getUserId();
        String username = UserContext.getUsername();

        // 将用户信息添加到Feign请求头，传递给下游服务
        if (userId != null) {
            template.header(JwtConstants.USER_ID_HEADER, String.valueOf(userId));
            log.debug("Feign调用传递用户ID: {}", userId);
        }

        if (username != null) {
            template.header(JwtConstants.USERNAME_HEADER, username);
            log.debug("Feign调用传递用户名: {}", username);
        }
    }
}
