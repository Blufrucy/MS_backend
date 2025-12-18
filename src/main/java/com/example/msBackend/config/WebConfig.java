package com.example.msBackend.config;

import com.example.msBackend.inttercptor.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private TokenInterceptor tokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/api/**") // 只拦截 /api/** 路径
                .excludePathPatterns(
                        "/api/auth/base/**",           // 排除用户登录/注册等路径
                        "/api/admin/auth/**",          // 排除管理员登录接口
                        "/api/pay/callback/**",        // 排除支付回调接口（第三方调用）
                        "/api/seckill/base/**"         // 排除秒杀预热接口（无需登录）
                );
    }
}
