package com.liubingqi.seckill.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class StockSpecVo {

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
     *  规格库存
     */
    private Integer stock;

    /**
     * 售出数量
     */
    private Integer saleNum;

    /**
     * 颜色
     */
    private String color;

    /**
     * 规格
     */
    private String productSpec;
}
