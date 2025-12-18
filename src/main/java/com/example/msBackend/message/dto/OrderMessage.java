package com.example.msBackend.message.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OrderMessage implements Serializable {
    private Long seckillProductId;
    private Long userId;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal totalAmount;
    private String imageUrl;
    private Integer addressId; // 收货地址ID
}
