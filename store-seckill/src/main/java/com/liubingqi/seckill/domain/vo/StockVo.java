package com.liubingqi.seckill.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 秒杀库存展示/下单校验 VO
 */
@Data
@Schema(description = "秒杀库存展示/下单校验 VO")
public class StockVo {

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "商品图片url")
    private String imageUrl;

    @Schema(description = "规格ID")
    private Long specId;

    @Schema(description = "商品名称")
    private String productName;

    @Schema(description = "规格")
    private String productSpec;

    @Schema(description = "颜色")
    private String color;

    @Schema(description = "规格价格")
    private BigDecimal specPrice;
}
