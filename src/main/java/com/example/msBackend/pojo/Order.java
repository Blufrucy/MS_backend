package com.example.msBackend.pojo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Order {
    private Long id;              // 订单ID
    private String orderNo;       // 订单编号
    private Long userId;          // 用户ID
    private Long productId;       // 商品ID
    private Long seckillProductId; // 秒杀商品ID
    private String productName;   // 商品名称
    private Integer quantity;     // 购买数量
    private BigDecimal totalAmount; // 订单总金额
    private Integer status;       // 订单状态(0:待支付,1:已支付,2:已取消)
    private String imageUrl;      // 商品图片URL
    private LocalDateTime createdAt;   // 创建时间
    private LocalDateTime paymentTime; // 支付时间
    private LocalDateTime updateTime;  // 更新时间
}