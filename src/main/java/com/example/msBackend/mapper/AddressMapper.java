package com.example.msBackend.mapper;

import com.example.msBackend.pojo.Address;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AddressMapper {

    /**
     * 插入地址
     * @param address 地址信息
     */
    @Insert("INSERT INTO address(user_id, receiver_name, phone, province, city, district, detail_address, is_default, create_time, update_time) " +
            "VALUES(#{userId}, #{receiverName}, #{phone}, #{province}, #{city}, #{district}, #{detailAddress}, #{isDefault}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Address address);

    /**
     * 根据ID删除地址
     * @param id 地址ID
     */
    @Delete("DELETE FROM address WHERE id = #{id}")
    void delete(Long id);

    /**
     * 更新地址信息
     * @param address 地址信息
     */
    @Update("UPDATE address SET receiver_name = #{receiverName}, phone = #{phone}, province = #{province}, " +
            "city = #{city}, district = #{district}, detail_address = #{detailAddress}, " +
            "is_default = #{isDefault}, update_time = #{updateTime} WHERE id = #{id}")
    void update(Address address);

    /**
     * 根据ID查询地址
     * @param id 地址ID
     * @return 地址信息
     */
    @Select("SELECT * FROM address WHERE id = #{id}")
    Address findById(Long id);

    /**
     * 根据用户ID查询地址列表
     * @param userId 用户ID
     * @return 地址列表
     */
    @Select("SELECT * FROM address WHERE user_id = #{userId} ORDER BY is_default DESC, create_time DESC")
    List<Address> findByUserId(Long userId);

    /**
     * 将用户的所有地址设为非默认
     * @param userId 用户ID
     */
    @Update("UPDATE address SET is_default = 0 WHERE user_id = #{userId}")
    void updateAllToNonDefault(Long userId);

    /**
     * 将指定地址设为默认
     * @param addressId 地址ID
     */
    @Update("UPDATE address SET is_default = 1 WHERE id = #{addressId}")
    void updateToDefault(Long addressId);
}