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
                .addPathPatterns("/**") // 拦截所有路径
                .excludePathPatterns(
                        "/api/auth/base/**",           // 排除登录/注册等路径
                        "/api/pay/callback/**" ,   // 排除支付回调接口（第三方调用）
                        "/admin/**",
                        "/seckill/base/**"

                );
    }
}
