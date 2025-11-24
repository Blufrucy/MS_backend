package com.example.msBackend.mapper;

import com.example.msBackend.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User finUser(User user);

    void register(User user);
}
