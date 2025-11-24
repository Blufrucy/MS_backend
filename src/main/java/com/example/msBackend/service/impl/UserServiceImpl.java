package com.example.msBackend.service.impl;

import com.example.msBackend.Util.RedisAccountUtil;
import com.example.msBackend.mapper.UserMapper;
import com.example.msBackend.pojo.User;
import com.example.msBackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    //生成账号
    @Autowired
    private RedisAccountUtil redisAccountUtil;
    @Override
    public Boolean register(User user) {
        //检查注册的邮箱是否已注册
        User u = userMapper.finUser(user);
        if(u!=null){
            return false;
        }
        String acc =  redisAccountUtil.generateIncrementAccount();
        user.setUsername(acc);
        userMapper.register(user);
        return true;
    }

    @Override
    public User login(User user) {
        User u = userMapper.finUser(user);
        //账号不存在
        if(u==null){
            return null;
        }
        //密码错误
        if(!user.getPassword().equals(u.getPassword())){
            return null;
        }
        return u;
    }
}
