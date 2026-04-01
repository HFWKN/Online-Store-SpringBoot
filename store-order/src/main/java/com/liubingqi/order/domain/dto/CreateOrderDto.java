package com.liubingqi.order.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "用户下单/创建订单请求 DTO（后端接收前端参数）")
public class CreateOrderDto {

    @NotNull
    @Positive
    @Schema(description = "收货地址ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long addressId;
    @Valid
    @NotEmpty
    @Schema(description = "下单明细（至少一项）", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<OrderItemDto> items;

    @Schema(description = "买家备注")
    @Size(max = 200)
    private String remark;

    // 用户支付的金额
    @Positive
    @Schema(description = "用户支付的金额", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal payAmount;

    @Data
    @Schema(description = "下单商品明细 DTO")
    public static class OrderItemDto {

        @NotNull
        @Schema(description = "商品ID", requiredMode = Schema.RequiredMode.REQUIRED)
        private Long productId;

        @NotNull
        @Schema(description = "规格ID")
        private Long specId;

        @NotNull
        @Positive
        @Schema(description = "购买数量", requiredMode = Schema.RequiredMode.REQUIRED)
        private Integer num;
    }
}
