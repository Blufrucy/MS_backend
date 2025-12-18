package com.example.msBackend.controller;

import com.example.msBackend.Util.MinioUtils;
import com.example.msBackend.pojo.Product;
import com.example.msBackend.pojo.SeckillProduct;
import com.example.msBackend.pojo.Vo.ResultVo;
import com.example.msBackend.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    // ========== 普通商品接口（重构，整合图片上传） ==========
    /**
     * 添加商品（含图片上传）
     * @param file 商品图片（必须传）
     * @param name 商品名称
     * @param description 商品描述
     * @param originalPrice 商品原价
     * @param status 商品状态（默认1）
     * @return 操作结果
     */
    @PostMapping("/addProduct")
    public ResultVo addProduct(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("originalPrice") String originalPrice,
            @RequestParam(value = "status", defaultValue = "1") Integer status
    ) {
        try {
            // 1. 上传图片到MinIO并获取URL
            String imageUrl = uploadImageToMinio(file);
            if (imageUrl == null) {
                return ResultVo.error("图片上传失败");
            }

            // 2. 构建商品对象
            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setOriginalPrice(new java.math.BigDecimal(originalPrice));
            product.setImageUrl(imageUrl);
            product.setStatus(status);

            // 3. 调用Service添加商品
            return adminService.addProduct(product);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVo.error("添加商品失败：" + e.getMessage());
        }
    }

    /**
     * 更新商品（支持图片更新）
     * @param id 商品ID（必须传）
     * @param file 商品图片（可选，不传则不更新图片）
     * @param name 商品名称（可选）
     * @param description 商品描述（可选）
     * @param originalPrice 商品原价（可选）
     * @param status 商品状态（可选）
     * @return 操作结果
     */
    @PostMapping("/updateProduct")
    public ResultVo updateProduct(
            @RequestParam("id") Long id,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "originalPrice", required = false) String originalPrice,
            @RequestParam(value = "status", required = false) Integer status
    ) {
        try {
            // 1. 构建商品对象
            Product product = new Product();
            product.setId(id);
            product.setName(name);
            product.setDescription(description);
            if (originalPrice != null && !originalPrice.isEmpty()) {
                product.setOriginalPrice(new java.math.BigDecimal(originalPrice));
            }
            product.setStatus(status);

            // 2. 如果传了新图片，上传并更新URL
            if (file != null && !file.isEmpty()) {
                String newImageUrl = uploadImageToMinio(file);
                if (newImageUrl == null) {
                    return ResultVo.error("图片更新失败");
                }
                product.setImageUrl(newImageUrl);
            }

            // 3. 调用Service更新商品
            return adminService.updateProduct(product);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVo.error("更新商品失败：" + e.getMessage());
        }
    }

    /**
     * 删除商品
     */
    @GetMapping("/deleteProduct")
    public ResultVo deleteProduct(@RequestParam Long id) {
        return adminService.deleteProduct(id);
    }

    /**
     * 根据ID查询商品
     */
    @GetMapping("/getProduct")
    public ResultVo getProduct(@RequestParam Long id) {
        return adminService.getProductById(id);
    }

    /**
     * 查询商品列表
     */
    @GetMapping("/listProducts")
    public ResultVo listProducts(@RequestParam(required = false) Integer status) {
        return adminService.listProducts(status);
    }

    /**
     * 添加秒杀商品
     * @param seckillProduct
     * @return
     */
    @PostMapping("/addSeckillProduct")
    public ResultVo addSeckillProduct(@RequestBody SeckillProduct seckillProduct) {
        return adminService.addSeckillProduct(seckillProduct);
    }

    @GetMapping("/deleteSeckillProduct")
    public ResultVo deleteSeckillProduct(@RequestParam Long id) {
        return adminService.deleteSeckillProduct(id);
    }

    @PostMapping("/updateSeckillProduct")
    public ResultVo updateSeckillProduct(@RequestBody SeckillProduct seckillProduct) {
        return adminService.updateSeckillProduct(seckillProduct);
    }

    @GetMapping("/getSeckillProduct")
    public ResultVo getSeckillProduct(@RequestParam Long id) {
        return adminService.getSeckillProductById(id);
    }

    @GetMapping("/listSeckillProducts")
    public ResultVo listSeckillProducts(@RequestParam(required = false) Byte status) {
        return adminService.listSeckillProducts(status);
    }

    // ========== RESTful 风格秒杀商品接口 ==========
    /**
     * 添加秒杀商品（RESTful风格）
     * POST /api/admin/seckill/products
     */
    @PostMapping("/seckill/products")
    public ResultVo addSeckillProductRestful(@RequestBody SeckillProduct seckillProduct) {
        ResultVo result = adminService.addSeckillProduct(seckillProduct);
        if (result.getCode() == 200 && result.getData() != null) {
            // 返回格式：{"code": 200, "message": "添加成功", "data": {"id": 1}}
            return ResultVo.success("添加成功", new java.util.HashMap<String, Object>() {{
                put("id", result.getData());
            }});
        }
        return result;
    }

    /**
     * 更新秒杀商品（RESTful风格）
     * PUT /api/admin/seckill/products/{id}
     */
    @PutMapping("/seckill/products/{id}")
    public ResultVo updateSeckillProductRestful(@PathVariable Long id, @RequestBody SeckillProduct seckillProduct) {
        seckillProduct.setId(id);
        ResultVo result = adminService.updateSeckillProduct(seckillProduct);
        if (result.getCode() == 200) {
            return ResultVo.success("更新成功", new java.util.HashMap<String, Object>() {{
                put("id", id);
            }});
        }
        return result;
    }

    /**
     * 秒杀商品状态管理
     * PUT /api/admin/seckill/products/{id}/status
     */
    @PutMapping("/seckill/products/{id}/status")
    public ResultVo updateSeckillProductStatus(@PathVariable Long id, @RequestBody java.util.Map<String, Integer> request) {
        Integer status = request.get("status");
        if (status == null || (status != 0 && status != 1)) {
            return ResultVo.error("状态参数错误，必须为0(下架)或1(上架)");
        }
        return adminService.updateSeckillProductStatus(id, status.byteValue());
    }

    /**
     * 系统监控统计
     * GET /api/admin/monitor/stats
     */
    @GetMapping("/monitor/stats")
    public ResultVo getMonitorStats() {
        return adminService.getMonitorStats();
    }

    // ========== 私有工具方法：上传图片到MinIO ==========
    private String uploadImageToMinio(MultipartFile file) throws Exception {
        // 1. 校验文件
        if (file.isEmpty()) {
            return null;
        }
        // 限制文件大小（5MB）
        long fileSize = file.getSize();
        if (fileSize > 5 * 1024 * 1024) {
            return null;
        }
        // 限制图片格式（仅允许jpg/png/jpeg）
        String originalFilename = file.getOriginalFilename();
        String fileSuffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (!"jpg".equals(fileSuffix) && !"png".equals(fileSuffix) && !"jpeg".equals(fileSuffix)) {
            return null;
        }
        // 2. 生成唯一文件名
        String objectName = "product/" + UUID.randomUUID().toString() + "." + fileSuffix;
        // 3. 调用MinIO工具类上传
        return MinioUtils.uploadMultipartFile(file, objectName);
    }
}