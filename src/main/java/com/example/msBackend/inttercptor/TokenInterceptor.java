package com.example.msBackend.inttercptor;

import com.example.msBackend.Util.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private TokenUtil tokenUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("========== TokenInterceptor start ==========");
        log.info("URI = {}", request.getRequestURI());

        // 1. 跳过无需校验的路径
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/api/auth/login") || requestURI.startsWith("/api/auth/register") || requestURI.contains("/swagger/")) {
            return true;
        }

        // 2. 从请求头获取Token（约定：Authorization: Bearer {token}）
        String authHeader = request.getHeader("Authorization");
        log.info(">>> Authorization header = {}", authHeader);
        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            log.warn(">>> Token格式错误，header={}", authHeader);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"Token不存在或格式错误\"}");
            return false;
        }

        // 3. 提取Token并校验（Redis中是否存在）
        String token = authHeader.substring(7).trim();
        log.info(">>> 前端传来的token={}", token);
        if (!tokenUtil.validateToken(token)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"Token已过期或无效\"}");
            return false;
        }

        // 4. 解析用户ID并存入请求属性
        String userId = tokenUtil.getUserIdFromToken(token);
        request.setAttribute("userId", userId);

        log.info("userId set to request attribute: {}", userId);

        return true;
    }
}