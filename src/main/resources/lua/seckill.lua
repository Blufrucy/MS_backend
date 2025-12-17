-- 秒杀Lua脚本：原子检查库存、扣减库存、记录用户
-- KEYS[1]：秒杀商品库存key（seckill:stock:{seckillProductId}）
-- KEYS[2]：用户抢购记录key（seckill:user:{seckillProductId}）
-- ARGV[1]：用户ID
-- ARGV[2]：购买数量

-- 1. 检查用户是否已抢购
if redis.call('SISMEMBER', KEYS[2], ARGV[1]) == 1 then
    return 2 -- 已抢购
end

-- 2. 检查库存是否充足
local stock = tonumber(redis.call('GET', KEYS[1]))
local buyNum = tonumber(ARGV[2])
if not stock or stock < buyNum then
    return 0 -- 库存不足
end

-- 3. 扣减库存
redis.call('DECRBY', KEYS[1], buyNum)
-- 4. 记录用户
redis.call('SADD', KEYS[2], ARGV[1])
return 1 -- 秒杀成功