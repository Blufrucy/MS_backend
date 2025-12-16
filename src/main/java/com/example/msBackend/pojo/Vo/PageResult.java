package com.example.msBackend.pojo.Vo;

import lombok.Data;

import java.util.List;

/**
 * 分页结果封装类（泛型 T 为集合类型，如 List<Product>）
 */
@Data
public class PageResult<T> {
    private Long total; // 总条数
    private Integer totalPages; // 总页数
    private List<T> list; // 当前页数据（集合）
    private Integer pageNum; // 当前页码
    private Integer pageSize; // 每页条数
}