package com.example.msBackend.mapper;

import com.example.msBackend.pojo.Product;
import com.example.msBackend.pojo.SeckillProduct;
import com.example.msBackend.pojo.User;
import com.example.msBackend.pojo.Vo.SeckillProductVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserMapper {
    User finUser(User user);

    void register(User user);

    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Long id);

    @Update("UPDATE user SET nickname = #{nickname}, phone = #{phone}, email = #{email}, avatar = #{avatar}, update_time = CURRENT_TIMESTAMP WHERE id = #{id}")
    void update(User user);

    // 商品分页查询（仅接收搜索名称，无需分页参数）
    List<Product> listProductsByName(String productName);

    // 秒杀商品分页查询（返回VO，包含关联商品信息）
    List<SeckillProductVO> listSeckillProductsByName(String seckillProductName);
}
