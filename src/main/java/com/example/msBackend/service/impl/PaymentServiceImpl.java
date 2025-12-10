package com.example.msBackend.service.impl;

import com.example.msBackend.mapper.OrderMapper;
import com.example.msBackend.mapper.PaymentMapper;
import com.example.msBackend.pojo.Order;
import com.example.msBackend.pojo.Payment;
import com.example.msBackend.pojo.Vo.ResultVo;
import com.example.msBackend.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private OrderMapper orderMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 处理支付回调
     */
    @Override
    @Transactional
    public void handlePaymentCallback(Integer paymentType, Map<String, Object> callbackData) {
        try {
            log.info("开始处理支付回调，支付类型: {}, 回调数据: {}", paymentType, callbackData);
            
            // 从回调数据中提取订单号（不同支付平台字段名可能不同）
            String orderNo = extractOrderNo(callbackData, paymentType);
            
            log.info("提取的订单号: {}", orderNo);
            
            if (orderNo == null || orderNo.isEmpty()) {
                log.error("回调数据中未找到订单号，支付类型: {}, 回调数据: {}", paymentType, callbackData);
                throw new RuntimeException("订单号不存在");
            }

            // 查询订单
            Order order = orderMapper.findByOrderNo(orderNo);
            if (order == null) {
                log.error("订单不存在: {}", orderNo);
                throw new RuntimeException("订单不存在");
            }

            // 检查订单状态，避免重复处理
            if (order.getStatus() != null && order.getStatus() == 1) {
                log.info("订单已支付，无需重复处理: {}", orderNo);
                return;
            }

            // 查询或创建支付记录
            Payment payment = paymentMapper.findByOrderNo(orderNo);
            if (payment == null) {
                payment = new Payment();
                payment.setOrderNo(orderNo);
                payment.setUserId(order.getUserId());
                payment.setPaymentType(paymentType);
                payment.setAmount(order.getTotalAmount());
                payment.setPaid(1);
                payment.setPaymentTime(LocalDateTime.now());
                payment.setCallbackData(objectMapper.writeValueAsString(callbackData));
                payment.setCreateTime(LocalDateTime.now());
                payment.setUpdateTime(LocalDateTime.now());
                
                paymentMapper.insert(payment);
            } else {
                // 更新支付记录
                payment.setPaid(1);
                payment.setPaymentTime(LocalDateTime.now());
                payment.setCallbackData(objectMapper.writeValueAsString(callbackData));
                payment.setUpdateTime(LocalDateTime.now());
                
                paymentMapper.update(payment);
            }

            // 更新订单状态为已支付
            order.setStatus((byte) 1);
            order.setPaymentTime(LocalDateTime.now());
            order.setUpdateTime(LocalDateTime.now());
            orderMapper.updateStatus(order);

            log.info("支付回调处理成功，订单号: {}", orderNo);

        } catch (Exception e) {
            log.error("支付回调处理失败，支付类型: {}, 回调数据: {}", paymentType, callbackData, e);
            throw new RuntimeException("支付回调处理失败: " + e.getMessage(), e);
        }
    }

    /**
     * 查询支付状态
     */
    @Override
    public ResultVo getPaymentStatus(String orderNo, Long userId) {
        try {
            // 查询订单
            Order order = orderMapper.findByOrderNo(orderNo);
            
            if (order == null) {
                return ResultVo.error("订单不存在");
            }

            // 验证订单归属
            if (!order.getUserId().equals(userId)) {
                return ResultVo.error("无权查询该订单");
            }

            // 查询支付记录
            Payment payment = paymentMapper.findByOrderNo(orderNo);

            // 构建返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("orderNo", orderNo);
            
            // 安全地判断是否已支付
            boolean isPaid = false;
            LocalDateTime paymentTime = null;
            
            if (payment != null) {
                isPaid = payment.getPaid() != null && payment.getPaid() == 1;
                paymentTime = payment.getPaymentTime();
            }
            
            data.put("paid", isPaid);
            data.put("paymentTime", paymentTime);

            return ResultVo.success(data);
            
        } catch (Exception e) {
            log.error("查询支付状态失败，订单号: {}, 用户ID: {}", orderNo, userId, e);
            return ResultVo.error("查询支付状态失败: " + e.getMessage());
        }
    }

    /**
     * 从回调数据中提取订单号
     * 不同支付平台的字段名可能不同
     */
    private String extractOrderNo(Map<String, Object> callbackData, Integer paymentType) {
        // 微信支付
        if (paymentType == 1) {
            return (String) callbackData.get("out_trade_no");
        }
        // 支付宝
        else if (paymentType == 2) {
            return (String) callbackData.get("out_trade_no");
        }
        // 通用字段
        return (String) callbackData.get("orderNo");
    }
}
