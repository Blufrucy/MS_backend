package com.example.msBackend.pojo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
public class Product {
    private Long id;                // 商品ID
    private String name;            // 商品名称
    private String description;     // 商品描述
    private BigDecimal originalPrice; // 原价（修正为BigDecimal，对应数据库decimal）
    private String imageUrl;        // 商品图片URL（驼峰命名，符合Java规范）
    private Integer status = 1;     // 状态(1:正常,0:下架)，默认值修正为1
    private LocalDateTime createTime; // 创建时间（驼峰命名）
    private LocalDateTime updateTime; // 更新时间（驼峰命名）
}