package com.example.msBackend.service;

import com.example.msBackend.pojo.User;

public interface UserService {
    Boolean register(User user);

    User login(User user);
}
