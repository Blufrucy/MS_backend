package com.example.msBackend.controller;

import com.example.msBackend.pojo.Admin;
import com.example.msBackend.pojo.Vo.ResultVo;
import com.example.msBackend.service.AdminAuthService;
import com.example.msBackend.Util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {
    
    @Autowired
    private AdminAuthService adminAuthService;
    
    @Autowired
    private TokenUtil tokenUtil;
    
    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public ResultVo login(@RequestParam String username, @RequestParam String password) {
        try {
            Admin admin = adminAuthService.login(username, password);
            
            // 生成token并保存到Redis
            String token = TokenUtil.generateToken();
            tokenUtil.saveTokenToRedis(token, admin.getId());
            admin.setToken(token);
            
            return ResultVo.success("登录成功", admin);
        } catch (Exception e) {
            return ResultVo.error(e.getMessage());
        }
    }
    
    /**
     * 获取管理员信息
     */
    @GetMapping("/info")
    public ResultVo getInfo(@RequestAttribute("userId") String userId) {
        try {
            Admin admin = adminAuthService.getAdminById(Long.valueOf(userId));
            if (admin == null) {
                return ResultVo.error("管理员不存在");
            }
            return ResultVo.success(admin);
        } catch (Exception e) {
            return ResultVo.error(e.getMessage());
        }
    }
}
