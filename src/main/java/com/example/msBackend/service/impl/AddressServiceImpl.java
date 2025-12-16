package com.example.msBackend.service.impl;

import com.example.msBackend.mapper.AddressMapper;
import com.example.msBackend.pojo.Address;
import com.example.msBackend.pojo.Vo.ResultVo;
import com.example.msBackend.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public ResultVo addAddress(Address address) {
        // 设置创建时间和更新时间
        address.setCreateTime(LocalDateTime.now());
        address.setUpdateTime(LocalDateTime.now());

        // 如果没有设置是否默认，默认为0（非默认）
        if (address.getIsDefault() == null) {
            address.setIsDefault(0);
        }

        // 如果是第一个地址，自动设为默认地址
        List<Address> existingAddresses = addressMapper.findByUserId(address.getUserId());
        if (existingAddresses.isEmpty()) {
            address.setIsDefault(1);
        }

        addressMapper.insert(address);
        return ResultVo.success("地址添加成功");
    }

    @Override
    public ResultVo deleteAddress(Long addressId, Long userId) {
        // 先验证地址是否属于该用户
        Address address = addressMapper.findById(addressId);
        if (address == null) {
            return ResultVo.error("地址不存在");
        }

        if (!address.getUserId().equals(userId)) {
            return ResultVo.error("无权删除该地址");
        }

        addressMapper.delete(addressId);
        return ResultVo.success("地址删除成功");
    }

    @Override
    public ResultVo updateAddress(Address address) {
        // 验证地址是否属于该用户
        Address existingAddress = addressMapper.findById(address.getId());
        if (existingAddress == null) {
            return ResultVo.error("地址不存在");
        }

        if (!existingAddress.getUserId().equals(address.getUserId())) {
            return ResultVo.error("无权修改该地址");
        }

        // 设置更新时间
        address.setUpdateTime(LocalDateTime.now());

        addressMapper.update(address);
        return ResultVo.success("地址更新成功");
    }

    @Override
    public ResultVo<List<Address>> getAddressListByUserId(Long userId) {
        List<Address> addresses = addressMapper.findByUserId(userId);
        return ResultVo.success(addresses);
    }

    @Override
    public ResultVo<Address> getAddressById(Long addressId, Long userId) {
        Address address = addressMapper.findById(addressId);

        if (address == null) {
            return ResultVo.error("地址不存在");
        }

        if (!address.getUserId().equals(userId)) {
            return ResultVo.error("无权查看该地址");
        }

        return ResultVo.success(address);
    }

    @Override
    public ResultVo setDefaultAddress(Long addressId, Long userId) {
        // 验证地址是否属于该用户
        Address address = addressMapper.findById(addressId);
        if (address == null) {
            return ResultVo.error("地址不存在");
        }

        if (!address.getUserId().equals(userId)) {
            return ResultVo.error("无权设置该地址");
        }

        // 先将该用户的所有地址设为非默认
        addressMapper.updateAllToNonDefault(userId);

        // 再将指定地址设为默认
        addressMapper.updateToDefault(addressId);

        return ResultVo.success("默认地址设置成功");
    }
}