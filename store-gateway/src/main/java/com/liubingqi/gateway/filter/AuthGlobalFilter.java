package com.liubingqi.gateway.filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWT;
import com.liubingqi.gateway.utils.JwtTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 全局鉴权过滤器
 * 
 * AuthGlobalFilter（认证全局过滤器）

在请求到达具体服务之前进行身份验证
检查用户是否登录、token 是否有效
你说得对，这个确实需要配合 JWT 使用，现在写确实早了
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    // 注入JWT工具类
    private final JwtTool jwtTool;


    // 白名单路径，不需要token验证
    private static final List<String> WHITE_LIST = List.of(
            "/user/login",
            "/user/register", 
            "/user/captcha",
            "/product/list",
            "/product/detail",
            "/product/page"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        log.debug("请求路径: {}", path);

        // 白名单放行
        if (isWhitePath(path)) {
            return chain.filter(exchange);
        }

        // 获取token
        String token = request.getHeaders().getFirst("Authorization");

        if (StrUtil.isBlank(token)) {
            log.warn("请求未携带token: {}", path);
            return unauthorized(exchange.getResponse());
        }

        // 移除 Bearer 前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 校验token
        try {

            JWT jwt = jwtTool.parseToken(token);

            // 从JWT对象中获取用户信息
            Long userId = Long.valueOf(jwt.getPayload("userId").toString());
            String username = jwt.getPayload("username").toString();

            // 将用户信息传递给下游服务
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Id", String.valueOf(userId))
                    .header("X-User-Name", username)
                    .build();

            // 放行
            return chain.filter(exchange.mutate().request(modifiedRequest).build());

        }catch (Exception e){
            log.warn("JWT校验失败: {}, 路径: {}", e.getMessage(), path);
            return unauthorized(exchange.getResponse());
        }
    }

    /**
     * 判断是否是白名单路径（循环判断）
     */
    private boolean isWhitePath(String path) {
        for (String whitePattern : WHITE_LIST) {
            if (path.startsWith(whitePattern)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 返回未授权响应
     */
    private Mono<Void> unauthorized(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return -100; // 优先级高，先执行
    }
}
