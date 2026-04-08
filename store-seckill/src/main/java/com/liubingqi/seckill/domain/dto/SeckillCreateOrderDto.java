package com.liubingqi.seckill.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 秒杀下单请求 DTO（不含收货人详细信息，只传地址ID）
 */
@Data
@Schema(description = "秒杀下单请求 DTO")
public class SeckillCreateOrderDto {

    @NotNull
    @Positive
    @Schema(description = "收货地址ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long addressId;

    @NotNull
    @Positive
    @Schema(description = "秒杀库存ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long stockId;

    @NotNull
    @Positive
    @Schema(description = "秒杀活动ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long activityId;

    @NotNull
    @Positive
    @Schema(description = "商品ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long productId;

    @NotNull
    @Positive
    @Schema(description = "规格ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long specId;

    @NotNull
    @Positive
    @Schema(description = "购买数量", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer num;

    @NotNull
    @Positive
    @Schema(description = "用户提交的实付金额（后端会二次校验）", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal payAmount;

    @NotBlank
    @Schema(description = "秒杀令牌（防刷/防重复提交）", requiredMode = Schema.RequiredMode.REQUIRED)
    private String seckillToken;
}
