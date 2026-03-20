package com.liubingqi.product.service;

import com.liubingqi.product.domain.po.ProductComment;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 商品评价表 服务类
 * </p>
 *
 * @author lbq
 * @since 2026-03-20
 */
public interface IProductCommentService extends IService<ProductComment> {

    // 根据商品id查询其评论集合
    List<ProductComment> getCommentById(Integer productId);

    // 好评率
    Integer goodCommentRate(Integer productId);

    // 获取好评TOP10
    List<ProductComment> goodCommentTOP10();

}
