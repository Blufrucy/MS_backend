package com.example.msBackend.pojo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Product {
    private Long id;              // 商品ID
    private String name;          // 商品名称
    private String description;   // 商品描述
    private BigDecimal originalPrice;  // 原价
    private String imageUrl;      // 商品图片URL
    private Integer status;       // 状态(1:正常,0:下架)
    private LocalDateTime createTime;   // 创建时间
    private LocalDateTime updateTime;   // 更新时间
}