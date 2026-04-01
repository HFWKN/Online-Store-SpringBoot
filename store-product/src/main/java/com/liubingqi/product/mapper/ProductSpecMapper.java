package com.liubingqi.product.mapper;

import com.liubingqi.product.domain.po.ProductSpec;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 商品规格表 Mapper 接口
 * </p>
 *
 * @author lbq
 * @since 2026-03-23
 */
public interface ProductSpecMapper extends BaseMapper<ProductSpec> {

    // 用户下单
    Integer userPlaceAnOrder(@Param("num") Integer num,
                             @Param("productId") Long productId,
                             @Param("specId") Long specId);
}
