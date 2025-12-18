package com.example.msBackend.mapper;

import com.example.msBackend.pojo.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdminAuthMapper {
    
    /**
     * 根据用户名查询管理员
     */
    Admin findByUsername(@Param("username") String username);
    
    /**
     * 根据ID查询管理员
     */
    Admin findById(@Param("id") Long id);
}
