package com.example.msBackend.pojo;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 秒杀商品实体类（对应 seckill_product 表）
 * @Data 注解自动生成 getter/setter、toString、equals、hashCode、无参构造方法
 */
@Data
public class SeckillProduct {

    /**
     * 秒杀商品ID（自增主键）
     */
    private Long id;

    /**
     * 关联商品ID（非空，关联商品表主键）
     */
    private Long productId;

    /**
     * 秒杀价格（非空，精确到分）
     */
    private BigDecimal seckillPrice;

    /**
     * 总库存（非空，秒杀商品总数量）
     */
    private Integer stock;

    /**
     * 可用库存（非空，当前可抢购的库存数量）
     */
    private Integer availableStock;

    /**
     * 秒杀开始时间（非空，秒杀活动启动时间）
     */
    private LocalDateTime startTime;

    /**
     * 秒杀结束时间（非空，秒杀活动截止时间）
     */
    private LocalDateTime endTime;

    /**
     * 状态（非空，默认0：0-未开始，1-进行中，2-已结束）
     */
    private Byte status = 0; // 匹配表中 default 0

    /**
     * 创建时间（默认当前时间，可为空）
     */
    private LocalDateTime createTime;

    /**
     * 更新时间（默认当前时间，更新时自动刷新，可为空）
     */
    private LocalDateTime updateTime;
}