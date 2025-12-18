package com.example.msBackend.service.impl;

import com.example.msBackend.mapper.OrderMapper;
import com.example.msBackend.mapper.ProductMapper;
import com.example.msBackend.mapper.SeckillProductMapper;
import com.example.msBackend.mapper.UserSeckillRecordMapper;
import com.example.msBackend.message.dto.OrderMessage;
import com.example.msBackend.pojo.*;
import com.example.msBackend.service.SeckillActivityService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class SeckillActivityServiceImpl implements SeckillActivityService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private SeckillProductMapper seckillProductMapper;
    @Resource
    private ProductMapper productMapper;
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private UserSeckillRecordMapper userSeckillRecordMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    // Lua脚本加载
    private DefaultRedisScript<Integer> seckillScript;
    @PostConstruct
    public void initScript() {
        seckillScript = new DefaultRedisScript<>();
        seckillScript.setLocation(new ClassPathResource("lua/seckill.lua"));
        seckillScript.setResultType(Integer.class);
    }

    // Redis Key前缀
    private static final String SECKILL_STOCK_KEY = "seckill:stock:%s";
    private static final String SECKILL_USER_KEY = "seckill:user:%s";
    private static final String SECKILL_ORDER_QUEUE = "seckill:order:queue"; // Redis List队列

    // 线程池：消费订单队列
    private final ThreadPoolTaskExecutor orderExecutor = new ThreadPoolTaskExecutor();
    @PostConstruct
    public void initExecutor() {
        orderExecutor.setCorePoolSize(5);       // 核心线程数
        orderExecutor.setMaxPoolSize(10);       // 最大线程数
        orderExecutor.setQueueCapacity(1000);   // 队列容量
        orderExecutor.initialize();
        // 启动后台线程消费订单队列
        orderExecutor.execute(this::consumeOrderQueue);
    }

    @Override
    public void warmUpStock(Long seckillProductId) {
        SeckillProduct seckillProduct = seckillProductMapper.selectById(seckillProductId);
        if (seckillProduct == null || seckillProduct.getAvailableStock() <= 0) {
            throw new RuntimeException("秒杀商品不存在或库存不足");
        }

        // ========== 核心修复：校验秒杀时间有效性 ==========
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = seckillProduct.getEndTime();
        LocalDateTime startTime = seckillProduct.getStartTime();

        // 1. 校验结束时间是否晚于当前时间
        if (endTime.isBefore(now)) {
            throw new RuntimeException("秒杀商品已结束（结束时间：" + endTime + "），无法预热库存");
        }
        // 2. 可选：校验开始时间是否合理（未开始/进行中）
        if (startTime.isAfter(now.plusDays(7))) {
            throw new RuntimeException("秒杀商品开始时间过远（开始时间：" + startTime + "），暂不支持预热");
        }

        // ========== 计算有效过期时间（确保为正数） ==========
        // 基础过期时间：活动结束后1小时
        Duration expireDuration = Duration.between(now, endTime).plusHours(1);
        // 兜底：如果过期时间<1分钟，强制设为1小时（避免极短/负数过期时间）
        if (expireDuration.isNegative() || expireDuration.getSeconds() < 60) {
            expireDuration = Duration.ofHours(1);
        }

        // ========== 写入Redis（指定时间单位，避免歧义） ==========
        String stockKey = String.format(SECKILL_STOCK_KEY, seckillProductId);
        // 明确指定时间单位为秒（Redis默认，但显式声明更安全）
        stringRedisTemplate.opsForValue().set(
                stockKey,
                String.valueOf(seckillProduct.getAvailableStock()),
                expireDuration.getSeconds(),
                TimeUnit.SECONDS
        );

        // 初始化用户抢购记录Set（复用相同的过期时间）
        String userKey = String.format(SECKILL_USER_KEY, seckillProductId);
        stringRedisTemplate.opsForSet().add(userKey, "");
        stringRedisTemplate.expire(
                userKey,
                expireDuration.getSeconds(),
                TimeUnit.SECONDS
        );
    }

    @Override
    public Long doSeckill(Long seckillProductId, Long userId, Integer quantity, Integer addressId) {
        // 1. 执行Lua脚本
        String stockKey = String.format(SECKILL_STOCK_KEY, seckillProductId);
        String userKey = String.format(SECKILL_USER_KEY, seckillProductId);
        Object result = stringRedisTemplate.execute(
                seckillScript,
                Arrays.asList(stockKey, userKey),
                userId.toString(),
                quantity.toString()
        );

        // 2. 秒杀成功：将订单信息写入Redis List队列
        if ((Long) result == 1) {
            SeckillProduct seckillProduct = seckillProductMapper.selectById(seckillProductId);
            Product product = productMapper.findProductById(seckillProduct.getProductId());

            // 构建订单消息
            OrderMessage message = new OrderMessage();
            message.setSeckillProductId(seckillProductId);
            message.setUserId(userId);
            message.setProductId(product.getId());
            message.setProductName(product.getName());
            message.setQuantity(quantity);
            message.setTotalAmount(seckillProduct.getSeckillPrice().multiply(new BigDecimal(quantity)));
            message.setImageUrl(product.getImageUrl());
            message.setAddressId(addressId); // 设置收货地址ID

            // 投递到Redis List队列（左推）
            redisTemplate.opsForList().leftPush(SECKILL_ORDER_QUEUE, message);
        }
        return (Long) result;
    }

    // 后台线程：消费Redis List队列，创建订单
    private void consumeOrderQueue() {
        while (true) {
            try {
                // 右弹出队列（阻塞1秒，避免空轮询）
                OrderMessage message = (OrderMessage) redisTemplate.opsForList().rightPop(SECKILL_ORDER_QUEUE, 1, TimeUnit.SECONDS);
                if (message != null) {
                    createOrder(message); // 执行下单逻辑
                }
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    TimeUnit.SECONDS.sleep(1); // 异常时休眠1秒
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    // 事务性创建订单
    @Transactional(rollbackFor = Exception.class)
    public void createOrder(OrderMessage message) {
        try {
            System.out.println("========== 开始创建订单 ==========");
            System.out.println("用户ID: " + message.getUserId());
            System.out.println("秒杀商品ID: " + message.getSeckillProductId());
            System.out.println("商品名称: " + message.getProductName());
            System.out.println("数量: " + message.getQuantity());
            System.out.println("地址ID: " + message.getAddressId());

            // 1. 生成订单编号
            String orderNo = "SK" + UUID.randomUUID().toString().replace("-", "").substring(0, 20);
            System.out.println("生成订单号: " + orderNo);

            // 2. 插入订单
            Order order = new Order();
            order.setOrderNo(orderNo);
            order.setUserId(message.getUserId());
            order.setProductId(message.getProductId());
            order.setSeckillProductId(message.getSeckillProductId());
            order.setProductName(message.getProductName());
            order.setQuantity(message.getQuantity());
            order.setTotalAmount(message.getTotalAmount());
            order.setStatus((byte) 0); // 待支付
            order.setImageUrl(message.getImageUrl());
            order.setAddressId(message.getAddressId()); // 设置收货地址ID
            order.setCreatedAt(LocalDateTime.now());
            orderMapper.insert(order);
            System.out.println("订单插入成功");

            // 3. 检查是否已有秒杀记录
            UserSeckillRecord existingRecord = userSeckillRecordMapper.selectByUserIdAndSeckillId(
                    message.getUserId(), 
                    message.getSeckillProductId()
            );
            
            if (existingRecord != null) {
                System.out.println("警告：用户秒杀记录已存在，ID: " + existingRecord.getId());
                // 记录已存在，可能是之前取消订单时没有删除成功
                // 这里不抛出异常，继续执行
            } else {
                // 3. 插入用户秒杀记录（数据库唯一索引防重复）
                UserSeckillRecord record = new UserSeckillRecord();
                record.setUserId(message.getUserId());
                record.setSeckillProductId(message.getSeckillProductId());
                userSeckillRecordMapper.insert(record);
                System.out.println("秒杀记录插入成功");
            }

            // 4. 乐观锁更新数据库库存
            int affectRows = seckillProductMapper.updateAvailableStock(message.getSeckillProductId(), message.getQuantity());
            System.out.println("库存更新影响行数: " + affectRows);
            
            if (affectRows == 0) {
                // 库存不足：回滚Redis库存
                String stockKey = String.format(SECKILL_STOCK_KEY, message.getSeckillProductId());
                stringRedisTemplate.opsForValue().increment(stockKey, message.getQuantity());
                System.out.println("库存不足，回滚Redis库存");
                throw new RuntimeException("库存不足，下单失败");
            }
            
            System.out.println("========== 订单创建成功 ==========");
        } catch (Exception e) {
            System.err.println("========== 订单创建失败 ==========");
            System.err.println("错误信息: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}