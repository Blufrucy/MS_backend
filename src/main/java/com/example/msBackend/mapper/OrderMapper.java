package com.example.msBackend.mapper;

import com.example.msBackend.pojo.Order;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderMapper {
    
    /**
     * 根据订单号查询订单
     */
    Order findByOrderNo(String orderNo);
    
    /**
     * 根据订单ID查询订单
     */
    Order findById(Long orderId);
    
    /**
     * 根据用户ID查询订单列表
     */
    List<Order> findByUserId(Long userId);
    
    /**
     * 创建订单
     */
    void insert(Order order);
    
    /**
     * 更新订单状态
     */
    void updateStatus(Order order);
    
    /**
     * 取消订单
     */
    void cancelOrder(Long orderId);
}
