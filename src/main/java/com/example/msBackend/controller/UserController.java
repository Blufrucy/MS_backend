package com.example.msBackend.controller;

import com.example.msBackend.Util.EmailVerificationUtil;
import com.example.msBackend.Util.MinioUtils;
import com.example.msBackend.Util.TokenUtil;
import com.example.msBackend.pojo.*;
import com.example.msBackend.pojo.Vo.PageResult;
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
    @PostMapping("/base/register")
    public ResultVo register(@RequestBody User user){
        System.out.println("接收到的用户数据: " + user); // 添加这行日志

        boolean isValid = emailVerificationUtil.verifyCode(user.getEmail(), user.getCode());
        if (!isValid){
            return ResultVo.error("验证码错误或已过期");
        }
        Boolean b =  userService.register(user);
        if(b){
            return ResultVo.success(user);
        }
        return ResultVo.error("注册失败,用户已存在");
    }

    @PostMapping("/base/login")
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

    @PostMapping("/base/sendVerificationCode")
    public ResultVo sendVerificationCode(@RequestParam String email) {
        String code = emailVerificationUtil.generateVerificationCode();
        boolean success = emailVerificationUtil.sendVerificationCode(email, code);

        if (success) {
            return ResultVo.success("验证码发送成功");
        } else {
            return ResultVo.error("验证码发送失败");
        }
    }

    @PostMapping("/base/verifyCode")
    public ResultVo verifyCode(@RequestParam String email, @RequestParam String code) {
        boolean isValid = emailVerificationUtil.verifyCode(email, code);

        if (isValid) {
            return ResultVo.success("验证码验证成功");
        } else {
            return ResultVo.error("验证码错误或已过期");
        }
    }

    @PostMapping("update")
    public ResultVo updateUser(@RequestBody User user, HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");

        if (userId == null) {
            return ResultVo.error("无法获取用户信息");
        }

        user.setId(Long.parseLong(userId));
        return userService.updateUser(user);
    }



    /**
     * 分页查询商品（支持名称模糊搜索）
     * 不传productName则查询全部商品
     */
    @PostMapping("/base/product/page")
    public ResultVo<PageResult<Product>> listProductsByPage(@RequestBody ProductPageQuery query) {
        return userService.listProductsByPage(query);
    }

    /**
     * 分页查询秒杀商品（支持关联商品名称模糊搜索）
     * 不传seckillProductName则查询全部秒杀商品
     */
    @PostMapping("/base/seckillProduct/page")
    public ResultVo listSeckillProductsByPage(@RequestBody SeckillProductPageQuery query) {
        return userService.listSeckillProductsByPage(query);
    }

}
