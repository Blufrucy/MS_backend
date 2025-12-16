package com.example.msBackend.pojo;

import lombok.Data;

@Data
public class SeckillProductPageQuery {
    // 页码（默认1）
    private Integer pageNum = 1;
    // 每页条数（默认10）
    private Integer pageSize = 10;
    // 秒杀商品关联的商品名称（模糊搜索）
    private String seckillProductName;
}