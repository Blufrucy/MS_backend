package com.example.msBackend.service;

import com.example.msBackend.pojo.Admin;

public interface AdminAuthService {
    
    /**
     * 管理员登录
     */
    Admin login(String username, String password);
    
    /**
     * 根据ID获取管理员信息
     */
    Admin getAdminById(Long id);
}
