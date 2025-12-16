package com.example.msBackend.pojo;

import lombok.Data;

@Data
public class User {
    private long id;
    private String username;
    private String password;
    private String nickname;
    private String phone;
    private String email;
    private String avatar;
    private String token;
    private String code;//注册验证码
}
