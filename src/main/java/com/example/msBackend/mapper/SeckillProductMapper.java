package com.example.msBackend.mapper;

import com.example.msBackend.pojo.SeckillProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SeckillProductMapper {
    /**
     * 根据ID查询秒杀商品
     */
    SeckillProduct selectById(@Param("id") Long id);

    /**
     * 乐观锁更新可用库存（防超卖）
     * @param id 秒杀商品ID
     * @param num 扣减数量
     * @return 影响行数（0=更新失败，1=更新成功）
     */
    int updateAvailableStock(@Param("id") Long id, @Param("num") Integer num);

    /**
     * 更新秒杀商品状态（根据时间自动更新）
     */
    int updateStatusByTime(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 恢复库存（用于取消订单）
     * @param id 秒杀商品ID
     * @param num 恢复数量
     * @return 影响行数
     */
    int restoreStock(@Param("id") Long id, @Param("num") Integer num);
}