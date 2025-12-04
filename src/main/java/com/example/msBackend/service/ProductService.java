package com.example.msBackend.service;

import com.example.msBackend.pojo.Product;
import com.example.msBackend.pojo.SeckillProduct;
import com.example.msBackend.pojo.Vo.ResultVo;

import java.util.List;

public interface ProductService {

    /**
     * 获取秒杀商品列表
     */
    ResultVo<List<SeckillProduct>> getSeckillProductList();

    /**
     * 获取秒杀商品详情
     */
    ResultVo<SeckillProduct> getSeckillProductDetail(Long productId);

    /**
     * 获取普通商品详情
     */
    ResultVo<Product> getProductDetail(Long productId);

    /**
     * 更新秒杀商品状态
     */
    void updateSeckillProductStatus();
}