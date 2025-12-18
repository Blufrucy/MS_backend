package com.example.msBackend.pojo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类（对应 order 表）
 * @Data 注解自动生成 getter/setter、toString、equals、hashCode、无参构造方法
 */
@Data
public class Order {

    /**
     * 订单ID（自增主键）
     */
    private Long id;

    /**
     * 订单编号（唯一标识，非空）
     */
    private String orderNo;

    /**
     * 用户ID（非空）
     */
    private Long userId;

    /**
     * 商品ID（非空）
     */
    private Long productId;

    /**
     * 秒杀商品ID（非空）
     */
    private Long seckillProductId;

    /**
     * 商品名称（非空）
     */
    private String productName;

    /**
     * 购买数量（非空）
     */
    private Integer quantity;

    /**
     * 订单总金额（非空，精确到分）
     */
    private BigDecimal totalAmount;

    /**
     * 订单状态（非空，默认0：0-待支付，1-已支付，2-已取消）
     */
    private Byte status = 0; // 匹配表中 default 0

    /**
     * 商品图片URL（可为空）
     */
    private String imageUrl;

    private Integer addressId;

    /**
     * 创建时间（默认当前时间，可为空）
     * 注：数据库用 datetime，Java 推荐用 LocalDateTime（JDK8+），兼容时间处理
     */
    private LocalDateTime createdAt;

    /**
     * 支付时间（可为空）
     */
    private LocalDateTime paymentTime;

    /**
     * 更新时间（默认当前时间，更新时自动刷新，可为空）
     */
    private LocalDateTime updateTime;
}
