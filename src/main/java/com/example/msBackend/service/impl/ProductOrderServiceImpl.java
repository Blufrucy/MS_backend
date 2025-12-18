package com.example.msBackend.service.impl;

import com.example.msBackend.mapper.OrderMapper;
import com.example.msBackend.mapper.ProductMapper;
import com.example.msBackend.pojo.Order;
import com.example.msBackend.pojo.Product;
import com.example.msBackend.pojo.Vo.ResultVo;
import com.example.msBackend.service.ProductOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 普通商品订单服务实现
 */
@Service
public class ProductOrderServiceImpl implements ProductOrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVo createProductOrder(Long userId, Long productId, Integer quantity, Integer addressId) {
        try {
            System.out.println("========== 开始创建普通商品订单 ==========");
            System.out.println("用户ID: " + userId);
            System.out.println("商品ID: " + productId);
            System.out.println("数量: " + quantity);
            System.out.println("地址ID: " + addressId);

            // 1. 查询商品信息
            Product product = productMapper.findProductById(productId);
            if (product == null) {
                return ResultVo.error("商品不存在");
            }

            if (product.getStatus() != 1) {
                return ResultVo.error("商品已下架");
            }

            // 2. 生成订单编号
            String orderNo = "PD" + UUID.randomUUID().toString().replace("-", "").substring(0, 20);
            System.out.println("生成订单号: " + orderNo);

            // 3. 计算订单金额
            BigDecimal totalAmount = product.getOriginalPrice().multiply(new BigDecimal(quantity));

            // 4. 创建订单
            Order order = new Order();
            order.setOrderNo(orderNo);
            order.setUserId(userId);
            order.setProductId(productId);
            order.setSeckillProductId(null); // 普通商品订单，秒杀商品ID为null
            order.setProductName(product.getName());
            order.setQuantity(quantity);
            order.setTotalAmount(totalAmount);
            order.setStatus((byte) 0); // 待支付
            order.setImageUrl(product.getImageUrl());
            order.setAddressId(addressId);
            order.setCreatedAt(LocalDateTime.now());

            orderMapper.insert(order);
            System.out.println("订单创建成功，订单ID: " + order.getId());

            System.out.println("========== 普通商品订单创建成功 ==========");

            return ResultVo.success("订单创建成功", order);
        } catch (Exception e) {
            System.err.println("========== 普通商品订单创建失败 ==========");
            System.err.println("错误信息: " + e.getMessage());
            e.printStackTrace();
            return ResultVo.error("订单创建失败：" + e.getMessage());
        }
    }
}
