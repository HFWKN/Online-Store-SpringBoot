package com.liubingqi.common.feignClient.product.vo;

import lombok.Data;

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
     * 颜色
     */
    private String color;

    /**
     * 规格
     */
    private String spec;
}
