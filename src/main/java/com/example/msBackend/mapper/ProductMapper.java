package com.example.msBackend.mapper;

import com.example.msBackend.pojo.Product;
import com.example.msBackend.pojo.SeckillProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ProductMapper {

    /**
     * 获取所有秒杀商品
     */
    @Select("SELECT sp.*, p.name, p.description, p.original_price, p.image_url " +
            "FROM seckill_product sp " +
            "LEFT JOIN product p ON sp.product_id = p.id " +
            "WHERE sp.status = 1 AND sp.start_time <= NOW() AND sp.end_time >= NOW()")
    List<SeckillProduct> findAllSeckillProducts();

    /**
     * 根据ID获取秒杀商品详情
     */
    @Select("SELECT sp.*, p.name, p.description, p.original_price, p.image_url " +
            "FROM seckill_product sp " +
            "LEFT JOIN product p ON sp.product_id = p.id " +
            "WHERE sp.id = #{id}")
    SeckillProduct findSeckillProductById(Long id);

    /**
     * 根据ID获取普通商品
     */
    @Select("SELECT * FROM product WHERE id = #{id} AND status = 1")
    Product findProductById(Long id);

    /**
     * 更新秒杀商品库存
     */
    @Update("UPDATE seckill_product SET available_stock = available_stock - #{quantity}, update_time = CURRENT_TIMESTAMP WHERE id = #{productId} AND available_stock >= #{quantity}")
    int updateSeckillStock(Long productId, Integer quantity);

    /**
     * 更新秒杀商品状态
     */
    @Update("UPDATE seckill_product SET status = #{status}, update_time = CURRENT_TIMESTAMP WHERE id = #{id}")
    int updateSeckillStatus(Long id, Integer status);

    /**
     * 获取需要更新状态的秒杀商品
     */
    @Select("SELECT * FROM seckill_product WHERE status != #{status} AND (start_time <= #{now} OR end_time <= #{now})")
    List<SeckillProduct> findSeckillProductsToUpdateStatus(LocalDateTime now, Integer status);
}