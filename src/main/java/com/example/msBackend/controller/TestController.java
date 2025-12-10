package com.example.msBackend.controller;

import com.example.msBackend.mapper.OrderMapper;
import com.example.msBackend.mapper.PaymentMapper;
import com.example.msBackend.pojo.Order;
import com.example.msBackend.pojo.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@CrossOrigin
public class TestController {

    @Autowired(required = false)
    private OrderMapper orderMapper;

    @Autowired(required = false)
    private PaymentMapper paymentMapper;

    @GetMapping("/mapper")
    public String testMapper() {
        StringBuilder result = new StringBuilder();
        
        result.append("OrderMapper: ").append(orderMapper != null ? "OK" : "NULL").append("\n");
        result.append("PaymentMapper: ").append(paymentMapper != null ? "OK" : "NULL").append("\n");
        
        if (orderMapper != null) {
            try {
                Order order = orderMapper.findByOrderNo("SK20251210123456789");
                result.append("Order query: ").append(order != null ? "Found" : "Not Found").append("\n");
                if (order != null) {
                    result.append("  - Order ID: ").append(order.getId()).append("\n");
                    result.append("  - User ID: ").append(order.getUserId()).append("\n");
                    result.append("  - Status: ").append(order.getStatus()).append("\n");
                    result.append("  - Amount: ").append(order.getTotalAmount()).append("\n");
                }
            } catch (Exception e) {
                result.append("Order query error: ").append(e.getMessage()).append("\n");
                e.printStackTrace();
            }
        }
        
        if (paymentMapper != null) {
            try {
                Payment payment = paymentMapper.findByOrderNo("SK20251210123456789");
                result.append("Payment query: ").append(payment != null ? "Found" : "Not Found").append("\n");
                if (payment != null) {
                    result.append("  - Payment ID: ").append(payment.getId()).append("\n");
                    result.append("  - Paid: ").append(payment.getPaid()).append("\n");
                }
            } catch (Exception e) {
                result.append("Payment query error: ").append(e.getMessage()).append("\n");
                e.printStackTrace();
            }
        }
        
        return result.toString();
    }
    
    @PostMapping("/callback-debug")
    public String testCallback(@RequestBody java.util.Map<String, Object> data) {
        StringBuilder result = new StringBuilder();
        result.append("Received callback data:\n");
        result.append("Data: ").append(data).append("\n");
        result.append("out_trade_no: ").append(data.get("out_trade_no")).append("\n");
        result.append("orderNo: ").append(data.get("orderNo")).append("\n");
        return result.toString();
    }
}
