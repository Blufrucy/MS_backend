package com.example.msBackend.controller;

import com.example.msBackend.pojo.Order;
import com.example.msBackend.pojo.Vo.ResultVo;
import com.example.msBackend.service.OrderService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.xml.transform.Result;

@RestController
@CrossOrigin
@RequestMapping("/api/Order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 根据订单号orderNo,修改订单信息
     * @param order
     * @return
     */
    @PostMapping("base/updateOrder")
    public ResultVo updateOrder(@RequestBody Order order){
         return orderService.updateOrder(order);
    }

    /**
     * 获取全部订单
     * @return
     */
    @PostMapping("base/listOrder")
    public ResultVo listOrder(){
        return orderService.listOrder();
    }

    /**
     * 根据订单号,查询订单
     * @param orderNo
     * @return
     */
    @GetMapping("base/selectByOrderNo")
    public ResultVo selectByOrderNo(@RequestParam String orderNo){
        return orderService.selectByOrderNo(orderNo);
    }


    /**
     * 根据用户id userId查询订单
     */
    @GetMapping("/base/selectOrderByUserId")
    public ResultVo selectOrderByUserId(@RequestParam long UserId){
        return orderService.selectOrderByUserId(UserId);
    }
}
