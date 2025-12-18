package com.example.msBackend.pojo;

import lombok.Data;

@Data
public class Admin {
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String email;
    private String avatar;
    private Integer status;  // 1-启用, 0-禁用
    private String token;    // 用于前端存储token
}
