package com.example.msBackend.Util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component // 确保被Spring扫描为Bean（需保证包路径在@ComponentScan范围内）
public class TokenUtil {

    // 1. 移除static修饰，改为实例字段
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Value("${token.expire}")
    private long expireSeconds; // Token过期时间（秒）

    @Value("${token.prefix}")
    private String tokenPrefix; // Redis Key前缀

    /**
     * 生成Token：UUID去掉下划线（保留static，仅工具方法无依赖）
     */
    public static String generateToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 存储Token到Redis（移除static，改为实例方法）
     */
    public void saveTokenToRedis(String token, long userId) {
        log.info("Token过期时间：{}秒", expireSeconds);
        log.info("Redis Token前缀：{}", tokenPrefix);
        stringRedisTemplate.opsForValue()
                .set(tokenPrefix + token, String.valueOf(userId), expireSeconds, TimeUnit.SECONDS);
    }

    /**
     * 校验Token有效性（移除static）
     */
    public boolean validateToken(String token) {
        return stringRedisTemplate.hasKey(tokenPrefix + token);
    }

    /**
     * 从Redis中获取Token对应的用户ID（移除static）
     */
    public String getUserIdFromToken(String token) {
        return stringRedisTemplate.opsForValue().get(tokenPrefix + token);
    }

    /**
     * 登出：删除Redis中的Token（移除static）
     */
    public void deleteToken(String token) {
        stringRedisTemplate.delete(tokenPrefix + token);
    }
}