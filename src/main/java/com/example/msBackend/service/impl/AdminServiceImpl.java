package com.example.msBackend.service.impl;

import com.example.msBackend.mapper.AdminMapper;
import com.example.msBackend.pojo.Product;
import com.example.msBackend.pojo.SeckillProduct;
import com.example.msBackend.pojo.Vo.ResultVo;
import com.example.msBackend.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminMapper adminMapper;

    /**
     * 添加商品
     */
    @Override
    public ResultVo addProduct(Product product) {
        try {
            // 参数校验
            if (!StringUtils.hasText(product.getName())) {
                return ResultVo.error("商品名称不能为空");
            }
            if (product.getOriginalPrice() == null) {
                return ResultVo.error("商品原价不能为空");
            }
            // 设置默认状态（如果前端未传）
            if (product.getStatus() == null) {
                product.setStatus(1); // 默认上架
            }
            // 执行添加
            adminMapper.addProduct(product);
            // 返回成功（包含商品ID）
            return ResultVo.success(product.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVo.error("添加商品失败：" + e.getMessage());
        }
    }

    /**
     * 删除商品
     */
    @Override
    public ResultVo deleteProduct(Long id) {
        try {
            if (id == null || id <= 0) {
                return ResultVo.error("商品ID不合法");
            }
            // 先查询商品是否存在
            Product product = adminMapper.getProductById(id);
            if (product == null) {
                return ResultVo.error("商品不存在，删除失败");
            }
            // 执行删除
            adminMapper.deleteProductById(id);
            return ResultVo.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVo.error("删除商品失败：" + e.getMessage());
        }
    }

    /**
     * 更新商品
     */
    @Override
    public ResultVo updateProduct(Product product) {
        try {
            // 参数校验
            if (product.getId() == null || product.getId() <= 0) {
                return ResultVo.error("商品ID不合法");
            }
            // 先查询商品是否存在
            Product existProduct = adminMapper.getProductById(product.getId());
            if (existProduct == null) {
                return ResultVo.error("商品不存在，更新失败");
            }
            // 执行更新
            adminMapper.updateProduct(product);
            return ResultVo.success("更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVo.error("更新商品失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID查询商品
     */
    @Override
    public ResultVo getProductById(Long id) {
        try {
            if (id == null || id <= 0) {
                return ResultVo.error("商品ID不合法");
            }
            Product product = adminMapper.getProductById(id);
            if (product == null) {
                return ResultVo.error("商品不存在");
            }
            return ResultVo.success(product);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVo.error("查询商品失败：" + e.getMessage());
        }
    }

    /**
     * 查询商品列表
     */
    @Override
    public ResultVo listProducts(Integer status) {
        try {
            List<Product> productList = adminMapper.listProducts(status);
            return ResultVo.success(productList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVo.error("查询商品列表失败：" + e.getMessage());
        }
    }

    @Override
    public ResultVo addSeckillProduct(SeckillProduct seckillProduct) {
        try {
            // 参数校验
            if (seckillProduct.getProductId() == null || seckillProduct.getProductId() <= 0) {
                return ResultVo.error("关联商品ID不合法");
            }
            if (seckillProduct.getSeckillPrice() == null || seckillProduct.getSeckillPrice().compareTo(BigDecimal.ZERO) <= 0) {
                return ResultVo.error("秒杀价格必须大于0");
            }
            if (seckillProduct.getStock() == null || seckillProduct.getStock() <= 0) {
                return ResultVo.error("总库存必须大于0");
            }
            if (seckillProduct.getAvailableStock() == null) {
                // 可用库存默认等于总库存
                seckillProduct.setAvailableStock(seckillProduct.getStock());
            }
            if (seckillProduct.getStartTime() == null) {
                return ResultVo.error("秒杀开始时间不能为空");
            }
            if (seckillProduct.getEndTime() == null) {
                return ResultVo.error("秒杀结束时间不能为空");
            }
            if (seckillProduct.getStartTime().isAfter(seckillProduct.getEndTime())) {
                return ResultVo.error("开始时间不能晚于结束时间");
            }
            // 校验关联商品是否存在
            Product product = adminMapper.getProductById(seckillProduct.getProductId());
            if (product == null) {
                return ResultVo.error("关联的普通商品不存在");
            }
            // 自动计算状态（根据时间）
            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(seckillProduct.getStartTime())) {
                seckillProduct.setStatus((byte) 0); // 未开始
            } else if (now.isAfter(seckillProduct.getEndTime())) {
                seckillProduct.setStatus((byte) 2); // 已结束
            } else {
                seckillProduct.setStatus((byte) 1); // 进行中
            }
            // 执行添加
            adminMapper.addSeckillProduct(seckillProduct);
            return ResultVo.success(seckillProduct.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVo.error("添加秒杀商品失败：" + e.getMessage());
        }
    }

    @Override
    public ResultVo deleteSeckillProduct(Long id) {
        try {
            if (id == null || id <= 0) {
                return ResultVo.error("秒杀商品ID不合法");
            }
            // 校验秒杀商品是否存在
            SeckillProduct seckillProduct = adminMapper.getSeckillProductById(id);
            if (seckillProduct == null) {
                return ResultVo.error("秒杀商品不存在，删除失败");
            }
            // 执行删除
            adminMapper.deleteSeckillProductById(id);
            return ResultVo.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVo.error("删除秒杀商品失败：" + e.getMessage());
        }
    }

    @Override
    public ResultVo updateSeckillProduct(SeckillProduct seckillProduct) {
        try {
            // 参数校验
            if (seckillProduct.getId() == null || seckillProduct.getId() <= 0) {
                return ResultVo.error("秒杀商品ID不合法");
            }
            // 校验秒杀商品是否存在
            SeckillProduct existSeckill = adminMapper.getSeckillProductById(seckillProduct.getId());
            if (existSeckill == null) {
                return ResultVo.error("秒杀商品不存在，更新失败");
            }
            // 校验关联商品
            if (seckillProduct.getProductId() != null) {
                Product product = adminMapper.getProductById(seckillProduct.getProductId());
                if (product == null) {
                    return ResultVo.error("关联的普通商品不存在");
                }
            }
            // 时间校验
            if (seckillProduct.getStartTime() != null && seckillProduct.getEndTime() != null) {
                if (seckillProduct.getStartTime().isAfter(seckillProduct.getEndTime())) {
                    return ResultVo.error("开始时间不能晚于结束时间");
                }
            }
            // 自动更新状态（如果传了时间）
            if (seckillProduct.getStartTime() != null && seckillProduct.getEndTime() != null) {
                LocalDateTime now = LocalDateTime.now();
                if (now.isBefore(seckillProduct.getStartTime())) {
                    seckillProduct.setStatus((byte) 0);
                } else if (now.isAfter(seckillProduct.getEndTime())) {
                    seckillProduct.setStatus((byte) 2);
                } else {
                    seckillProduct.setStatus((byte) 1);
                }
            }
            // 执行更新
            adminMapper.updateSeckillProduct(seckillProduct);
            return ResultVo.success("更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVo.error("更新秒杀商品失败：" + e.getMessage());
        }
    }

    @Override
    public ResultVo getSeckillProductById(Long id) {
        try {
            if (id == null || id <= 0) {
                return ResultVo.error("秒杀商品ID不合法");
            }
            SeckillProduct seckillProduct = adminMapper.getSeckillProductById(id);
            if (seckillProduct == null) {
                return ResultVo.error("秒杀商品不存在");
            }
            return ResultVo.success(seckillProduct);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVo.error("查询秒杀商品失败：" + e.getMessage());
        }
    }

    @Override
    public ResultVo listSeckillProducts(Byte status) {
        try {
            List<SeckillProduct> seckillProductList = adminMapper.listSeckillProducts(status);
            return ResultVo.success(seckillProductList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVo.error("查询秒杀商品列表失败：" + e.getMessage());
        }
    }
}