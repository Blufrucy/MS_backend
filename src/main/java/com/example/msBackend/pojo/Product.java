package com.example.msBackend.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Product {
    private long id;
    private String name;
    private String description;
    private String original_price;
    private String image_url;
    private int status = 0;//状态 0 下架 1 上架
    private LocalDateTime create_time;
    private LocalDateTime update_time;
}
