package com.example.msBackend.Util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisAccountUtil {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // Redis中存储递增序号的Key
    private static final String ACCOUNT_SEQ_KEY = "account:seq";

    /**
     * 生成递增账号：前缀 + 8位递增序号（如ms00000001、ms00000002）
     */
    public String generateIncrementAccount() {
        // INCR是原子操作，即使并发也不会重复
        long seq = stringRedisTemplate.opsForValue().increment(ACCOUNT_SEQ_KEY, 1);
        // 初始化序号（可选，避免首次为null）
        if (seq == 1) {
            stringRedisTemplate.expire(ACCOUNT_SEQ_KEY, 365, TimeUnit.DAYS); // 永久有效
        }
        // 补0为8位
        String seqStr = String.format("%08d", seq);
        return "ms" + seqStr;
    }
}