package com.example.msBackend.service;

import com.example.msBackend.pojo.Order;
import com.example.msBackend.pojo.Vo.ResultVo;

import javax.xml.transform.Result;

public interface OrderService {
    ResultVo updateOrder(Order order);

    ResultVo listOrder();

    ResultVo selectByOrderNo(String orderNo);

    ResultVo selectOrderByUserId(long userId);
}
