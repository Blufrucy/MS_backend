package com.example.msBackend.pojo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Address {
    private Long id;            // 地址ID
    private Long userId;        // 用户ID
    private String receiverName; // 收货人姓名
    private String phone;       // 收货人电话
    private String province;    // 省份
    private String city;        // 城市
    private String district;    // 区/县
    private String detailAddress; // 详细地址
    private Integer isDefault;  // 是否默认地址(1:是,0:否)
    private LocalDateTime createTime;  // 创建时间
    private LocalDateTime updateTime;  // 更新时间
}