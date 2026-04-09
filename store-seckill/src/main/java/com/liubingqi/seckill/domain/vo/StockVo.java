package com.liubingqi.seckill.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 秒杀库存展示/下单校验 VO
 */
@Data
@Schema(description = "秒杀库存展示/下单校验 VO")
public class StockVo {

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "商品名称")
    private String productName;

    @Schema(description = "商品图片url")
    private String imageUrl;

    // 商品规格信息
    List<StockSpecVo> specList;
}
