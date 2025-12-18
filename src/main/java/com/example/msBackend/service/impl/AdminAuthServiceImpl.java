package com.example.msBackend.service.impl;

import com.example.msBackend.mapper.AdminAuthMapper;
import com.example.msBackend.pojo.Admin;
import com.example.msBackend.service.AdminAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminAuthServiceImpl implements AdminAuthService {
    
    @Autowired
    private AdminAuthMapper adminAuthMapper;
    
    @Override
    public Admin login(String username, String password) {
        Admin admin = adminAuthMapper.findByUsername(username);
        
        if (admin == null) {
            throw new RuntimeException("管理员账号不存在");
        }
        
        if (admin.getStatus() == 0) {
            throw new RuntimeException("管理员账号已被禁用");
        }
        
        // 简单密码验证（实际项目应该使用加密）
        if (!admin.getPassword().equals(password)) {
            throw new RuntimeException("密码错误");
        }
        
        // 清除密码，不返回给前端
        admin.setPassword(null);
        
        return admin;
    }
    
    @Override
    public Admin getAdminById(Long id) {
        Admin admin = adminAuthMapper.findById(id);
        if (admin != null) {
            admin.setPassword(null);
        }
        return admin;
    }
}
