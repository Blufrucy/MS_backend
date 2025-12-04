package com.example.msBackend.mapper;

import com.example.msBackend.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {
    User finUser(User user);

    void register(User user);

    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Long id);

    @Update("UPDATE user SET nickname = #{nickname}, phone = #{phone}, email = #{email}, avatar = #{avatar}, update_time = CURRENT_TIMESTAMP WHERE id = #{id}")
    void update(User user);
}
