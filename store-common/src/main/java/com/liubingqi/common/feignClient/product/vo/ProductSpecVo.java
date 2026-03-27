package com.liubingqi.common.feignClient.product.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductSpecVo {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 关联商品ID (store_product.id)
     */
    private Integer productId;

    /**
     *  规格价格
     */
    private BigDecimal specPrice;

    /**
     * 颜色
     */
    private String color;

    /**
     * 规格
     */
    private String productSpec;
}
