package com.example.msBackend.service;

import com.example.msBackend.pojo.Address;
import com.example.msBackend.pojo.Vo.ResultVo;

import java.util.List;

public interface AddressService {

    /**
     * 添加地址
     * @param address 地址信息
     * @return 添加结果
     */
    ResultVo addAddress(Address address);

    /**
     * 删除地址
     * @param addressId 地址ID
     * @param userId 用户ID
     * @return 删除结果
     */
    ResultVo deleteAddress(Long addressId, Long userId);

    /**
     * 更新地址
     * @param address 地址信息
     * @return 更新结果
     */
    ResultVo updateAddress(Address address);

    /**
     * 根据用户ID获取地址列表
     * @param userId 用户ID
     * @return 地址列表
     */
    ResultVo<List<Address>> getAddressListByUserId(Long userId);

    /**
     * 根据地址ID获取地址详情
     * @param addressId 地址ID
     * @param userId 用户ID
     * @return 地址详情
     */
    ResultVo<Address> getAddressById(Long addressId, Long userId);

    /**
     * 设置默认地址
     * @param addressId 地址ID
     * @param userId 用户ID
     * @return 设置结果
     */
    ResultVo setDefaultAddress(Long addressId, Long userId);
}