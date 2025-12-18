package com.example.msBackend.service;

import com.example.msBackend.pojo.Vo.ResultVo;

/**
 * 普通商品订单服务接口
 */
public interface ProductOrderService {
    /**
     * 创建普通商品订单
     * @param userId 用户ID
     * @param productId 商品ID
     * @param quantity 购买数量
     * @param addressId 收货地址ID
     * @return 订单创建结果
     */
    ResultVo createProductOrder(Long userId, Long productId, Integer quantity, Integer addressId);
}
