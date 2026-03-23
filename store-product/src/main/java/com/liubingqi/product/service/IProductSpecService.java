package com.liubingqi.product.service;

import com.liubingqi.product.domain.po.ProductSpec;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liubingqi.product.domain.vo.ProductSpecVo;

import java.util.List;

/**
 * <p>
 * 商品规格表 服务类
 * </p>
 *
 * @author lbq
 * @since 2026-03-23
 */
public interface IProductSpecService extends IService<ProductSpec> {

    // 查询该商品的全部规格
    List<ProductSpecVo> selectAll(Long productId);
}
