package com.example.msBackend.pojo.Vo;

import com.example.msBackend.pojo.SeckillProduct;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 秒杀商品VO（包含关联商品的名称、图片）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SeckillProductVO extends SeckillProduct {
    // 关联商品名称
    private String productName;
    // 关联商品图片URL
    private String productImageUrl;
}