package com.example.msBackend.pojo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Payment {
    private Long id;           // ID
    private String orderNo;    // 订单编号
    private Long userId;       // 用户ID
    private Integer paymentType; // 支付方式(1:微信,2:支付宝)
    private BigDecimal amount; // 支付金额
    private Integer paid;      // 是否支付(1:是,0:否)
    private LocalDateTime paymentTime; // 支付时间
    private String callbackData; // 支付回调数据
    private LocalDateTime createTime;  // 创建时间
    private LocalDateTime updateTime;  // 更新时间
}