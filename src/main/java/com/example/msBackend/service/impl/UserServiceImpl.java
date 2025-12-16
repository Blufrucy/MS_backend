package com.example.msBackend.service.impl;

import com.example.msBackend.Util.RedisAccountUtil;
import com.example.msBackend.mapper.UserMapper;
import com.example.msBackend.pojo.*;
import com.example.msBackend.pojo.Vo.PageResult;
import com.example.msBackend.pojo.Vo.ResultVo;
import com.example.msBackend.pojo.Vo.SeckillProductVO;
import com.example.msBackend.service.UserService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    //生成账号
    @Autowired
    private RedisAccountUtil redisAccountUtil;

    @Override
    public Boolean register(User user) {
        //检查注册的邮箱是否已注册
        User u = userMapper.finUser(user);
        if (u != null) {
            return false;
        }
        String acc = redisAccountUtil.generateIncrementAccount();
        user.setUsername(acc);
        userMapper.register(user);
        return true;
    }

    @Override
    public User login(User user) {
        User u = userMapper.finUser(user);
        //账号不存在
        if (u == null) {
            return null;
        }
        //密码错误
        if (!user.getPassword().equals(u.getPassword())) {
            return null;
        }
        return u;
    }

    @Override
    public User findById(Long id) {
        return userMapper.findById(id);
    }

    @Override
    public ResultVo<User> updateUser(User user) {
        userMapper.update(user);
        User updatedUser = userMapper.findById(user.getId());
        updatedUser.setPassword("***");
        return ResultVo.success(updatedUser);
    }


    @Override
    public ResultVo<PageResult<Product>> listProductsByPage(ProductPageQuery query) {
        try {
            // 1. 参数校验
            Integer pageNum = query.getPageNum() == null || query.getPageNum() < 1 ? 1 : query.getPageNum();
            Integer pageSize = query.getPageSize() == null || query.getPageSize() < 1 || query.getPageSize() > 100 ? 10 : query.getPageSize();

            // 2. 启用 PageHelper 分页（核心：拦截后续第一个查询）
            PageHelper.startPage(pageNum, pageSize);

            // 3. 查询数据（无需手动加 LIMIT，PageHelper 自动处理；支持名称模糊搜索）
            List<Product> productList = userMapper.listProductsByName(query.getProductName());

            // 4. 封装 Page 结果（PageHelper 会返回 Page 类型，包含总条数/总页数）
            Page<Product> page = (Page<Product>) productList;

            // 5. 组装自定义分页结果
            PageResult<Product> pageResult = new PageResult<>();
            pageResult.setTotal(page.getTotal()); // 总条数
            pageResult.setTotalPages(page.getPages()); // 总页数
            pageResult.setList(page.getResult()); // 当前页数据（List<Product>）
            pageResult.setPageNum(page.getPageNum()); // 当前页码
            pageResult.setPageSize(page.getPageSize()); // 每页条数

            return ResultVo.success(pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVo.error("分页查询商品失败：" + e.getMessage());
        }
    }

    @Override
    public ResultVo<PageResult<SeckillProductVO>> listSeckillProductsByPage(SeckillProductPageQuery query) {
        try {
            // 1. 参数校验
            Integer pageNum = query.getPageNum() == null || query.getPageNum() < 1 ? 1 : query.getPageNum();
            Integer pageSize = query.getPageSize() == null || query.getPageSize() < 1 || query.getPageSize() > 100 ? 10 : query.getPageSize();

            // 2. 启用PageHelper分页
            PageHelper.startPage(pageNum, pageSize);

            // 3. 查询秒杀商品（返回VO，包含关联商品信息）
            List<SeckillProductVO> seckillProductList = userMapper.listSeckillProductsByName(query.getSeckillProductName());

            // 4. 封装Page结果
            Page<SeckillProductVO> page = (Page<SeckillProductVO>) seckillProductList;

            // 5. 组装分页结果
            PageResult<SeckillProductVO> pageResult = new PageResult<>();
            pageResult.setTotal(page.getTotal());
            pageResult.setTotalPages(page.getPages());
            pageResult.setList(page.getResult()); // List<SeckillProductVO>
            pageResult.setPageNum(page.getPageNum());
            pageResult.setPageSize(page.getPageSize());

            return ResultVo.success(pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVo.error("分页查询秒杀商品失败：" + e.getMessage());
        }
    }
}
