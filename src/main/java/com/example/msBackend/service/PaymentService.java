package com.example.msBackend.service;

import com.example.msBackend.pojo.Vo.ResultVo;

import java.util.Map;

public interface PaymentService {
    
    /**
     * 处理支付回调
     * @param paymentType 支付类型(1:微信,2:支付宝)
     * @param callbackData 回调数据
     */
    void handlePaymentCallback(Integer paymentType, Map<String, Object> callbackData);
    
    /**
     * 查询支付状态
     * @param orderNo 订单号
     * @param userId 用户ID
     * @return 支付状态信息
     */
    ResultVo getPaymentStatus(String orderNo, Long userId);
}
