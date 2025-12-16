package com.example.msBackend.controller;

import com.example.msBackend.pojo.Address;
import com.example.msBackend.pojo.Vo.ResultVo;
import com.example.msBackend.service.AddressService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/auth/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    /**
     * 添加地址
     * @param address 地址信息
     * @param request HTTP请求
     * @return 添加结果
     */
    @PostMapping("/add")
    public ResultVo addAddress(@RequestBody Address address, HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");

        if (userId == null) {
            return ResultVo.error("无法获取用户信息");
        }

        address.setUserId(Long.parseLong(userId));
        return addressService.addAddress(address);
    }

    /**
     * 删除地址
     * @param addressId 地址ID
     * @param request HTTP请求
     * @return 删除结果
     */
    @DeleteMapping("/delete/{addressId}")
    public ResultVo deleteAddress(@PathVariable Long addressId, HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");

        if (userId == null) {
            return ResultVo.error("无法获取用户信息");
        }

        return addressService.deleteAddress(addressId, Long.parseLong(userId));
    }

    /**
     * 更新地址
     * @param address 地址信息
     * @param request HTTP请求
     * @return 更新结果
     */
    @PutMapping("/update")
    public ResultVo updateAddress(@RequestBody Address address, HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");

        if (userId == null) {
            return ResultVo.error("无法获取用户信息");
        }

        address.setUserId(Long.parseLong(userId));
        return addressService.updateAddress(address);
    }

    /**
     * 获取当前用户的所有地址
     * @param request HTTP请求
     * @return 地址列表
     */
    @GetMapping("/list")
    public ResultVo<List<Address>> getAddressList(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");

        if (userId == null) {
            return ResultVo.error("无法获取用户信息");
        }

        return addressService.getAddressListByUserId(Long.parseLong(userId));
    }

    /**
     * 获取单个地址详情
     * @param addressId 地址ID
     * @param request HTTP请求
     * @return 地址详情
     */
    @GetMapping("/detail/{addressId}")
    public ResultVo<Address> getAddressDetail(@PathVariable Long addressId, HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");

        if (userId == null) {
            return ResultVo.error("无法获取用户信息");
        }

        return addressService.getAddressById(addressId, Long.parseLong(userId));
    }

    /**
     * 设置默认地址
     * @param addressId 地址ID
     * @param request HTTP请求
     * @return 设置结果
     */
    @PostMapping("/setDefault/{addressId}")
    public ResultVo setDefaultAddress(@PathVariable Long addressId, HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");

        if (userId == null) {
            return ResultVo.error("无法获取用户信息");
        }

        return addressService.setDefaultAddress(addressId, Long.parseLong(userId));
    }
}