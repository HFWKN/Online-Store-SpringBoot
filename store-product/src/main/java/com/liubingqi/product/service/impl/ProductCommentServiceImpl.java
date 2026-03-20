package com.liubingqi.product.service.impl;

import com.liubingqi.product.domain.po.ProductComment;
import com.liubingqi.product.mapper.ProductCommentMapper;
import com.liubingqi.product.service.IProductCommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 商品评价表 服务实现类
 * </p>
 *
 * @author lbq
 * @since 2026-03-20
 */
@Service
public class ProductCommentServiceImpl extends ServiceImpl<ProductCommentMapper, ProductComment> implements IProductCommentService {


    /**
     *  根据商品id查询其评论集合
     * @param productId
     * @return
     */
    @Override
    public List<ProductComment> getCommentById(Integer productId) {
        List<ProductComment> list = lambdaQuery()
                .eq(ProductComment::getStatus, 0) // 只查询未隐藏的
                .eq(ProductComment::getProductId, productId) // 根据商品id查询
                .list();

        return list;
    }


    /**
     *  好评率
     * @param productId
     * @return
     */
    @Override
    public Integer goodCommentRate(Integer productId) {
        // 1. 统计该商品有效评论总数
        long totalCount = lambdaQuery()
                .eq(ProductComment::getStatus, 0)
                .eq(ProductComment::getProductId, productId)
                .count();

        // 2. 如果没有评论，直接返回 0，避免除以零异常
        if (totalCount == 0) {
            return 0;
        }

        // 3. 统计好评数量 (4-5 星)
        long goodCount = lambdaQuery()
                .eq(ProductComment::getStatus, 0)
                .eq(ProductComment::getProductId, productId)
                .ge(ProductComment::getStarRating, 4)
                .count();

        // 4. 计算百分比
        return (int) ((goodCount * 100) / totalCount);
    }


    /**
     *  获取好评TOP10
     * @return
     */
    @Override
    public List<ProductComment> goodCommentTOP10() {
        // 按商品id分组，查询每个商品的评论为4-5星的数量，获取前10个
        return lambdaQuery()
                .eq(ProductComment::getStatus, 0) // 只查询未隐藏的
                .ge(ProductComment::getStarRating, 4) // 4-5星
                .groupBy(ProductComment::getProductId) // 按商品id分组
                .orderByDesc(ProductComment::getProductId, ProductComment::getStarRating) // 按商品id降序，按5星降序
                .last("limit 10") // 获取前10个
                .list();
    }
}
