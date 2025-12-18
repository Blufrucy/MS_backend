package com.example.msBackend.controller;

import com.example.msBackend.pojo.Vo.ResultVo;
import com.example.msBackend.service.ProductOrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 普通商品订单控制器
 */
@RestController
@CrossOrigin
@RequestMapping("/api/product/order")
public class ProductOrderController {

    @Autowired
    private ProductOrderService productOrderService;

    /**
     * 创建普通商品订单
     * @param params 订单参数
     * @param request HTTP请求
     * @return 订单创建结果
     */
    @PostMapping("/create")
    public ResultVo createOrder(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        
        if (userId == null) {
            return ResultVo.error("请先登录");
        }

        Long productId = Long.valueOf(params.get("productId").toString());
        Integer quantity = Integer.valueOf(params.get("quantity").toString());
        Integer addressId = params.get("addressId") != null ? 
            Integer.valueOf(params.get("addressId").toString()) : null;

        return productOrderService.createProductOrder(Long.valueOf(userId), productId, quantity, addressId);
    }
}
