package com.example.msBackend.mapper;

import com.example.msBackend.pojo.UserSeckillRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserSeckillRecordMapper {
    /**
     * 新增秒杀记录（数据库唯一索引防重复）
     */
    int insert(UserSeckillRecord record);

    /**
     * 查询用户是否已抢购该商品
     */
    UserSeckillRecord selectByUserIdAndSeckillId( Long userId, Long seckillProductId);
}
