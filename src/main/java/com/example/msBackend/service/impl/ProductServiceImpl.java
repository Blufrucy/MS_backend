package com.example.msBackend.service.impl;

import com.example.msBackend.mapper.ProductMapper;
import com.example.msBackend.pojo.Product;
import com.example.msBackend.pojo.SeckillProduct;
import com.example.msBackend.pojo.Vo.ResultVo;
import com.example.msBackend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public ResultVo<List<SeckillProduct>> getSeckillProductList() {
        try {
            List<SeckillProduct> seckillProducts = productMapper.findAllSeckillProducts();

            if (seckillProducts.isEmpty()) {
                return ResultVo.error("暂无秒杀商品");
            }

            // 设置原始价格和图片URL
            for (SeckillProduct product : seckillProducts) {
//                product.setOriginalPrice(product.getOriginalPrice());  需要在SeckillProduct类中设置相关函数（交给你们了）
//                product.setImage(product.getImageUrl()); 同上
            }

            return ResultVo.success(seckillProducts);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVo.error("获取秒杀商品列表失败");
        }
    }

    @Override
    public ResultVo<SeckillProduct> getSeckillProductDetail(Long productId) {
        try {
            SeckillProduct seckillProduct = productMapper.findSeckillProductById(productId);

            if (seckillProduct == null) {
                return ResultVo.error("秒杀商品不存在");
            }

            // 检查秒杀活动状态
            LocalDateTime now = LocalDateTime.now();
            if (seckillProduct.getStartTime().isAfter(now)) {
                return ResultVo.error("秒杀活动尚未开始");
            }

            if (seckillProduct.getEndTime().isBefore(now)) {
                return ResultVo.error("秒杀活动已结束");
            }

            if (seckillProduct.getStatus() != 1) {
                return ResultVo.error("秒杀商品不可用");
            }

            // 设置原始价格和图片URL
//            seckillProduct.setOriginalPrice(seckillProduct.getOriginalPrice()); 同上
//            seckillProduct.setImage(seckillProduct.getImageUrl()); 同上

            return ResultVo.success(seckillProduct);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVo.error("获取秒杀商品详情失败");
        }
    }

    @Override
    public ResultVo<Product> getProductDetail(Long productId) {
        try {
            Product product = productMapper.findProductById(productId);

            if (product == null) {
                return ResultVo.error("商品不存在或已下架");
            }

            if (product.getStatus() != 1) {
                return ResultVo.error("商品已下架");
            }

            return ResultVo.success(product);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVo.error("获取商品详情失败");
        }
    }

    @Override
    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    public void updateSeckillProductStatus() {
        LocalDateTime now = LocalDateTime.now();

        // 更新状态为进行中的商品
        List<SeckillProduct> productsToStart = productMapper.findSeckillProductsToUpdateStatus(now, 1);
        for (SeckillProduct product : productsToStart) {
            if (product.getStartTime().isBefore(now) && product.getEndTime().isAfter(now)) {
                productMapper.updateSeckillStatus(product.getId(), 1);
            }
        }

        // 更新状态为已结束的商品
        List<SeckillProduct> productsToEnd = productMapper.findSeckillProductsToUpdateStatus(now, 2);
        for (SeckillProduct product : productsToEnd) {
            if (product.getEndTime().isBefore(now)) {
                productMapper.updateSeckillStatus(product.getId(), 2);
            }
        }
    }
}