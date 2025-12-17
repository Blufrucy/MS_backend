package com.example.msBackend.service;

public interface SeckillActivityService {
    /**
     * 库存预热：将秒杀商品库存加载到Redis
     * @param seckillProductId 秒杀商品ID
     */
    void warmUpStock(Long seckillProductId);

    /**
     * 执行秒杀
     * @param seckillProductId 秒杀商品ID
     * @param userId 用户ID
     * @param quantity 购买数量
     * @return 秒杀结果（0:库存不足,1:成功,2:已抢购）
     */
    Long doSeckill(Long seckillProductId, Long userId, Integer quantity);
}