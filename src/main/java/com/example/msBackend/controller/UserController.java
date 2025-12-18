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
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;


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
        System.out.println("接收到的用户数据: " + user);

        // TODO: 开发环境临时禁用验证码验证，生产环境需要启用
        // 如果需要启用验证码，请先配置好邮箱服务，然后取消下面的注释
        /*
        boolean isValid = emailVerificationUtil.verifyCode(user.getEmail(), user.getCode());
        if (!isValid){
            return ResultVo.error("验证码错误或已过期");
        }
        */
        
        Boolean b = userService.register(user);
        if(b){
            // 注册成功后，user 对象已经包含了系统生成的 username
            // 隐藏密码，返回用户信息（包含 username）
            user.setPassword("***");
            
            // 构建返回信息
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("username", user.getUsername());
            result.put("nickname", user.getNickname());
            result.put("email", user.getEmail());
            result.put("phone", user.getPhone());
            result.put("message", "注册成功！您的用户名是：" + user.getUsername());
            
            return ResultVo.success(result);
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

    /**
     * 更新用户资料（含头像）
     * @param user    要更新的字段（JSON）
     * @param file    头像文件（可选）
     * @param request 请求
     * @return 更新结果
     */
    @PostMapping("update")
    public ResultVo updateUser(@RequestPart(value = "user", required = false) User user,
                               @RequestPart(value = "file", required = false) MultipartFile file,
                               HttpServletRequest request) {

        // 1. 统一取出 userId
        String userId = (String) request.getAttribute("userId");
        if (userId == null) {
            return ResultVo.error("无法获取用户信息");
        }
        long id = Long.parseLong(userId);

        // 2. 如果上传了头像，先处理文件
        if (file != null && !file.isEmpty()) {
            try {
                String fileName = "avatar/" + userId + "/" + UUID.randomUUID() + ".jpg";
                String avatarUrl = MinioUtils.uploadMultipartFile(file, fileName);

                // 保证 user 对象存在
                if (user == null) {
                    user = new User();
                }
                user.setAvatar(avatarUrl);
            } catch (Exception e) {
                e.printStackTrace();
                return ResultVo.error("头像上传失败: " + e.getMessage());
            }
        }

        // 3. 如果 user 为空，说明客户端啥也没传，直接报错
        if (user == null) {
            return ResultVo.error("缺少更新内容");
        }

        // 4. 补全 id 并调用 Service
        user.setId(id);
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
