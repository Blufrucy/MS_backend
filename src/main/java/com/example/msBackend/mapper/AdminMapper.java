package com.example.msBackend.mapper;

import com.example.msBackend.pojo.Product;
import com.example.msBackend.pojo.SeckillProduct;
import org.apache.ibatis.annotations.Mapper;


import com.example.msBackend.pojo.Product;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface AdminMapper {
    /**
     * 添加商品
     *  product 商品对象
     */
    void addProduct(Product product);

    /**
     * 根据ID删除商品
     *  id 商品ID
     */
    void deleteProductById(Long id);

    /**
     * 更新商品信息
     *  product 商品对象
     */
    void updateProduct(Product product);

    /**
     * 根据ID查询商品
     *  id 商品ID
     * @return 商品对象
     */
    Product getProductById(Long id);

    /**
     * 查询所有商品（支持状态筛选）
     *  status 商品状态（1:正常,0:下架，null查全部）
     * @return 商品列表
     */
    List<Product> listProducts(Integer status);
    /**
     * 添加秒杀商品
     */
    void addSeckillProduct(SeckillProduct seckillProduct);

    /**
     * 根据ID删除秒杀商品
     */
    void deleteSeckillProductById(Long id);

    /**
     * 更新秒杀商品
     */
    void updateSeckillProduct(SeckillProduct seckillProduct);

    /**
     * 根据ID查询秒杀商品
     */
    SeckillProduct getSeckillProductById(Long id);

    /**
     * 查询秒杀商品列表（支持状态筛选）
     */
    List<SeckillProduct> listSeckillProducts(Byte status);


}