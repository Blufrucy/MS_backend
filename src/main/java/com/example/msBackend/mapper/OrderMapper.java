package com.example.msBackend.mapper;

import com.example.msBackend.pojo.Order;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
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



    /**
     * 根据订单编号查询
     */
    Order selectByOrderNo( String orderNo);

    /**
     * 根据用户ID和秒杀商品ID查询订单
     */
    Order selectByUserIdAndSeckillId( Long userId, Long seckillProductId);

    /**
     * 更新订单支付状态
     */
    int updatePaymentStatus( String orderNo,  Integer status,  LocalDateTime paymentTime);

    /**
     * 查询用户未支付订单
     */
    List<Order> selectUnpaidByUserId( Long userId);
}
