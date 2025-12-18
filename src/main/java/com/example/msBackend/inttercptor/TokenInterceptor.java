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

        // 1. 跳过无需校验的路径（这些路径已在 WebConfig 中配置，但这里再次检查以确保安全）
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/api/auth/base/") || 
            requestURI.startsWith("/api/pay/callback/") ||
            requestURI.startsWith("/admin/") ||
            requestURI.startsWith("/seckill/base/") ||
            requestURI.contains("/swagger/")) {
            log.info(">>> 跳过Token验证，路径: {}", requestURI);
            return true;
        }

        // 2. 从请求头获取Token
        String authHeader = request.getHeader("Authorization");
        log.info(">>> Authorization header = {}", authHeader);
        
        // 如果没有 Authorization 头，直接返回 401
        if (authHeader == null || authHeader.isEmpty()) {
            log.warn(">>> Token不存在");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"未登录，请先登录\"}");
            return false;
        }
        
        // 支持两种格式：Bearer {token} 或直接 {token}
        String token;
        if (authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7).trim();
        } else {
            token = authHeader.trim();
        }
        
        if (token.isEmpty()) {
            log.warn(">>> Token为空");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"Token不存在或格式错误\"}");
            return false;
        }

        // 3. 校验Token（Redis中是否存在）
        log.info(">>> 前端传来的token={}", token);
        if (!tokenUtil.validateToken(token)) {
            log.warn(">>> Token无效或已过期");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"Token已过期或无效，请重新登录\"}");
            return false;
        }

        // 4. 解析用户ID并存入请求属性
        String userId = tokenUtil.getUserIdFromToken(token);
        request.setAttribute("userId", userId);

        log.info("userId set to request attribute: {}", userId);

        return true;
    }
}