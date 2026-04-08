package com.liubingqi.seckill.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 秒杀商品展示 VO
 */
@Data
@Schema(description = "秒杀商品展示 VO")
public class StockProductVo {

        @Schema(description = "商品ID")
        private Long productId;

        @Schema(description = "商品图片url")
        private String imageUrl;

        @Schema(description = "商品名称")
        private String productName;

        @Schema(description = "商品价格快照")
        private BigDecimal originPrice;

/*        @Schema(description = "规格")
        private String productSpec;*/

    }

