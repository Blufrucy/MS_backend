package com.example.msBackend.pojo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SeckillProduct {
    private Long id;              // 秒杀商品ID
    private Long productId;       // 关联商品ID
    private BigDecimal seckillPrice;  // 秒杀价格
    private Integer stock;        // 总库存
    private Integer availableStock;   // 可用库存
    private LocalDateTime startTime;  // 秒杀开始时间
    private LocalDateTime endTime;    // 秒杀结束时间
    private Integer status;       // 状态(0:未开始,1:进行中,2:已结束)
    private LocalDateTime createTime;  // 创建时间
    private LocalDateTime updateTime;  // 更新时间
}