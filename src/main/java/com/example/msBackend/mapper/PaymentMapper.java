package com.example.msBackend.mapper;

import com.example.msBackend.pojo.Payment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentMapper {
    
    /**
     * 根据订单号查询支付记录
     */
    Payment findByOrderNo(String orderNo);
    
    /**
     * 插入支付记录
     */
    void insert(Payment payment);
    
    /**
     * 更新支付记录
     */
    void update(Payment payment);
}
