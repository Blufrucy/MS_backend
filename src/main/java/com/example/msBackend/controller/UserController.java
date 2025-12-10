package com.example.msBackend.controller;

import com.example.msBackend.Util.EmailVerificationUtil;
import com.example.msBackend.Util.MinioUtils;
import com.example.msBackend.Util.TokenUtil;
import com.example.msBackend.pojo.User;
import com.example.msBackend.pojo.Vo.ResultVo;
import com.example.msBackend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@CrossOrigin
@RestController
@RequestMapping("/api/auth/")
public class UserController {
    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailVerificationUtil emailVerificationUtil;
    @PostMapping("register")
    public ResultVo register(@RequestBody User user){
        System.out.println("接收到的用户数据: " + user); // 添加这行日志
        Boolean b =  userService.register(user);
        if(b){
            return ResultVo.success(user);
        }
        return ResultVo.error("注册失败,用户已存在");
    }

    @PostMapping("login")
    public ResultVo login(@RequestBody User user) {
        User u = userService.login(user);
        if(u==null){
            return ResultVo.error("账号或密码错误");
        }
        String token = tokenUtil.generateToken();
        tokenUtil.saveTokenToRedis(token,u.getId());
        u.setToken(token);
        u.setPassword("***");
        return ResultVo.success(u);
    }

    @GetMapping("me")
    public ResultVo getUserInfo(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");

        if (userId == null) {
            return ResultVo.error("无法获取用户信息");
        }

        User user = userService.findById(Long.parseLong(userId));

        if (user == null) {
            return ResultVo.error("用户不存在");
        }

        // 隐藏密码
        user.setPassword("***");

        return ResultVo.success(user);
    }

    @PostMapping("sendVerificationCode")
    public ResultVo sendVerificationCode(@RequestParam String email) {
        String code = emailVerificationUtil.generateVerificationCode();
        boolean success = emailVerificationUtil.sendVerificationCode(email, code);

        if (success) {
            return ResultVo.success("验证码发送成功");
        } else {
            return ResultVo.error("验证码发送失败");
        }
    }

    @PostMapping("verifyCode")
    public ResultVo verifyCode(@RequestParam String email, @RequestParam String code) {
        boolean isValid = emailVerificationUtil.verifyCode(email, code);

        if (isValid) {
            return ResultVo.success("验证码验证成功");
        } else {
            return ResultVo.error("验证码错误或已过期");
        }
    }

    @PutMapping("update")
    public ResultVo updateUser(@RequestBody User user, HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");

        if (userId == null) {
            return ResultVo.error("无法获取用户信息");
        }

        user.setId(Long.parseLong(userId));
        return userService.updateUser(user);
    }
}
