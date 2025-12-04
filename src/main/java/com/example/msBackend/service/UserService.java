package com.example.msBackend.service;

import com.example.msBackend.pojo.User;
import com.example.msBackend.pojo.Vo.ResultVo;

public interface UserService {
    Boolean register(User user);

    User login(User user);

    User findById(Long id);

    ResultVo<User> updateUser(User user);
}
