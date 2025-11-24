package com.example.msBackend.controller;

import com.example.msBackend.Util.RedisAccountUtil;
import com.example.msBackend.Util.TokenUtil;
import com.example.msBackend.pojo.User;
import com.example.msBackend.pojo.Vo.ResultVo;
import com.example.msBackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@CrossOrigin
@RestController
@RequestMapping("/api/auth")
public class UserController {
    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private UserService userService;
    @PostMapping("register")
    public ResultVo register(@RequestBody User user){
        Boolean b =  userService.register(user);
        if(b){
            return ResultVo.success(user);
        }
        return ResultVo.error("注册失败,用户已存在");
    }

    @PostMapping("login")
    public ResultVo login(@RequestBody User user){
        User u = userService.login(user);
        if(u==null){
            return ResultVo.error("账号或密码错误");
        }
        String token = tokenUtil.generateToken();
        tokenUtil.saveTokenToRedis(token,1);
        u.setToken(token);
        u.setPassword("***");
        return ResultVo.success(u);
    }
}
