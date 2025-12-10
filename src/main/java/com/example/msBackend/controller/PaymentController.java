package com.example.msBackend.controller;

import com.example.msBackend.pojo.Vo.ResultVo;
import com.example.msBackend.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/pay")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    /**
     * 支付回调接口
     * 路径: /api/pay/callback/{paymentType}
     * 方法: POST
     * 描述: 第三方支付平台回调接口
     */
    @PostMapping("/callback/{paymentType}")
    public Map<String, String> paymentCallback(
            @PathVariable Integer paymentType,
            @RequestBody Map<String, Object> callbackData) {
        
        log.info("收到支付回调，支付类型: {}, 回调数据: {}", paymentType, callbackData);
        
        try {
            paymentService.handlePaymentCallback(paymentType, callbackData);
            return Map.of("code", "SUCCESS", "message", "支付成功");
        } catch (Exception e) {
            log.error("支付回调处理失败", e);
            return Map.of("code", "FAIL", "message", "处理失败");
        }
    }

    /**
     * 查询支付状态
     * 路径: /api/pay/status/{orderNo}
     * 方法: GET
     * 请求头: Authorization:{token}
     */
    @GetMapping("/status/{orderNo}")
    public ResultVo getPaymentStatus(
            @PathVariable String orderNo,
            HttpServletRequest request) {
        
        String userId = (String) request.getAttribute("userId");
        
        if (userId == null) {
            return ResultVo.error("无法获取用户信息");
        }
        
        log.info("查询支付状态，订单号: {}, 用户ID: {}", orderNo, userId);
        
        return paymentService.getPaymentStatus(orderNo, Long.parseLong(userId));
    }
}
