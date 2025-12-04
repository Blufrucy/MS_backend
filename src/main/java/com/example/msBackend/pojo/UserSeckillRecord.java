package com.example.msBackend.pojo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserSeckillRecord {
    private Long id;              // ID
    private Long userId;          // 用户ID
    private Long seckillProductId; // 秒杀商品ID
    private LocalDateTime createTime; // 创建时间
}