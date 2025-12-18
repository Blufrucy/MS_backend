package com.example.msBackend.controller;

import com.example.msBackend.pojo.Vo.ResultVo;
import com.example.msBackend.service.SeckillActivityService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/seckill")
public class SeckillActivityController {
    @Autowired
    private SeckillActivityService seckillActivityService;

    /**
     * 库存预热接口（管理员调用）
     */
    @PostMapping("base/warmUp")
    public ResultVo warmUpStock(@RequestParam Long seckillProductId) {
        seckillActivityService.warmUpStock(seckillProductId);
        return ResultVo.success("库存预热成功");
    }

    /**
     * 秒杀下单
     * @param seckillProductId 秒杀商品ID
     * @param request HTTP请求
     * @param quantity 购买数量
     * @param addressId 收货地址ID
     * @return
     */
    @PostMapping("doSeckill")
    public ResultVo doSeckill(
            @RequestParam Long seckillProductId,
            HttpServletRequest request,
            @RequestParam Integer quantity,
            @RequestParam(required = false) Integer addressId) {
        String userId = (String) request.getAttribute("userId");
        Long result = seckillActivityService.doSeckill(seckillProductId, Long.valueOf(userId), quantity, addressId);
        if (result == 0) {
            return ResultVo.error("手慢了，库存不足");
        } else if (result == 1) {
            return ResultVo.success("秒杀成功，正在生成订单");
        } else if (result == 2) {
            return ResultVo.error("您已抢购过该商品");
        }
        return ResultVo.error("系统错误");
    }
}
