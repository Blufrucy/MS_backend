package com.example.msBackend.service;

import com.example.msBackend.pojo.*;
import com.example.msBackend.pojo.Vo.PageResult;
import com.example.msBackend.pojo.Vo.ResultVo;
import com.example.msBackend.pojo.Vo.SeckillProductVO;

public interface UserService {
    Boolean register(User user);

    User login(User user);

    User findById(Long id);

    ResultVo<User> updateUser(User user);

    /**
     * 分页查询商品（支持名称模糊搜索）
     * @param query 分页+搜索参数
     * @return 分页结果
     */
    ResultVo<PageResult<Product>> listProductsByPage(ProductPageQuery query);

    /**
     * 分页查询秒杀商品（支持关联商品名称模糊搜索）
     * @param query 分页+搜索参数
     * @return 分页结果
     */
    ResultVo<PageResult<SeckillProductVO>> listSeckillProductsByPage(SeckillProductPageQuery query);
}
