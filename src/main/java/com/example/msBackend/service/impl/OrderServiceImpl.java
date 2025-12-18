package com.example.msBackend.service.impl;

import com.example.msBackend.mapper.OrderMapper;
import com.example.msBackend.mapper.SeckillProductMapper;
import com.example.msBackend.mapper.UserSeckillRecordMapper;
import com.example.msBackend.pojo.Order;
import com.example.msBackend.pojo.Vo.ResultVo;
import com.example.msBackend.service.OrderService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private UserSeckillRecordMapper userSeckillRecordMapper;
    
    @Autowired
    private SeckillProductMapper seckillProductMapper;
    
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    
    private static final String SECKILL_STOCK_KEY = "seckill:stock:%s";
    private static final String SECKILL_USER_KEY = "seckill:user:%s";

    /**
     * 修改订单
     * @param order
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVo updateOrder(Order order) {
        try {
            // 如果是取消订单（status=2），需要恢复库存和删除秒杀记录
            if (order.getStatus() != null && order.getStatus() == 2) {
                System.out.println("========== 开始取消订单 ==========");
                System.out.println("订单号: " + order.getOrderNo());
                
                // 查询原订单信息
                Order existingOrder = orderMapper.selectByOrderNo(order.getOrderNo());
                System.out.println("原订单状态: " + (existingOrder != null ? existingOrder.getStatus() : "null"));
                
                if (existingOrder != null && existingOrder.getStatus() == 0) {
                    // 只有待支付订单才能取消
                    Long seckillProductId = existingOrder.getSeckillProductId();
                    Long userId = existingOrder.getUserId();
                    Integer quantity = existingOrder.getQuantity();
                    
                    System.out.println("用户ID: " + userId);
                    System.out.println("秒杀商品ID: " + seckillProductId);
                    System.out.println("数量: " + quantity);
                    
                    // 1. 删除用户秒杀记录，恢复抢购资格
                    int deleteCount = userSeckillRecordMapper.deleteByUserIdAndSeckillId(userId, seckillProductId);
                    System.out.println("删除秒杀记录数: " + deleteCount);
                    
                    // 2. 恢复数据库库存
                    int restoreCount = seckillProductMapper.restoreStock(seckillProductId, quantity);
                    System.out.println("恢复库存影响行数: " + restoreCount);
                    
                    // 3. 恢复Redis库存
                    String stockKey = String.format(SECKILL_STOCK_KEY, seckillProductId);
                    Long newStock = stringRedisTemplate.opsForValue().increment(stockKey, quantity);
                    System.out.println("Redis库存恢复后: " + newStock);
                    
                    // 4. 从Redis Set中移除用户记录
                    String userKey = String.format(SECKILL_USER_KEY, seckillProductId);
                    Long removeCount = stringRedisTemplate.opsForSet().remove(userKey, userId.toString());
                    System.out.println("从Redis Set移除用户记录数: " + removeCount);
                    
                    System.out.println("========== 订单取消成功 ==========");
                } else {
                    System.out.println("订单不存在或状态不是待支付，无法取消");
                }
            }
            
            orderMapper.updateOrder(order);
            return ResultVo.success("修改成功");
        } catch (Exception e) {
            System.err.println("========== 订单更新失败 ==========");
            System.err.println("错误信息: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
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
