package com.example.msBackend.service.impl;

import com.example.msBackend.mapper.OrderMapper;
import com.example.msBackend.pojo.Order;
import com.example.msBackend.pojo.Vo.ResultVo;
import com.example.msBackend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 修改秒杀商品
     * @param order
     * @return
     */
    @Override
    public ResultVo updateOrder(Order order) {
        orderMapper.updateOrder(order);
        return ResultVo.success("修改成功");
    }


    /**
     * 获取全部订单
     * @return
     */
    @Override
    public ResultVo listOrder() {
        List<Order> orders = orderMapper.listOrder();
        if(orders.isEmpty() || orders == null){
            return ResultVo.success("未查询到任何订单");
        }
        return ResultVo.success(orders);
    }

    /**
     * 根据订单号orderNo查询订单
     * @param orderNo
     * @return
     */
    @Override
    public ResultVo selectByOrderNo(String orderNo) {
        Order order =  orderMapper.selectByOrderNo(orderNo);
        if(order==null){
            return ResultVo.success("未查询到任何订单");
        }
        return ResultVo.success(order);
    }

    @Override
    public ResultVo selectOrderByUserId(long userId) {
        List<Order> orders = orderMapper.selectOrderByUserId(userId);
        return ResultVo.success(orders);
    }


}
