package com.example.msBackend.service;

import com.example.msBackend.pojo.Product;
import com.example.msBackend.pojo.SeckillProduct;
import com.example.msBackend.pojo.Vo.ResultVo;

public interface AdminService {
    /**
     * 添加商品
     */
    ResultVo addProduct(Product product);

    /**
     * 根据ID删除商品
     */
    ResultVo deleteProduct(Long id);

    /**
     * 更新商品
     */
    ResultVo updateProduct(Product product);

    /**
     * 根据ID查询商品
     */
    ResultVo getProductById(Long id);

    /**
     * 查询商品列表（支持状态筛选）
     */
    ResultVo listProducts(Integer status);

    /**
     * 添加秒杀商品
     */
    ResultVo addSeckillProduct(SeckillProduct seckillProduct);

    /**
     * 删除秒杀商品
     */
    ResultVo deleteSeckillProduct(Long id);

    /**
     * 更新秒杀商品
     */
    ResultVo updateSeckillProduct(SeckillProduct seckillProduct);

    /**
     * 根据ID查询秒杀商品
     */
    ResultVo getSeckillProductById(Long id);

    /**
     * 查询秒杀商品列表
     */
    ResultVo listSeckillProducts(Byte status);

    /**
     * 更新秒杀商品状态
     */
    ResultVo updateSeckillProductStatus(Long id, Byte status);

    /**
     * 获取系统监控统计数据
     */
    ResultVo getMonitorStats();
}