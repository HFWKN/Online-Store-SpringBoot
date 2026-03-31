package com.liubingqi.order.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "用户下单/创建订单请求 DTO（后端接收前端参数）")
public class CreateOrderDto {

    @Schema(description = "收货地址ID")
    private Long addressId;

    @Schema(description = "收货人姓名")
    private String receiverName;

    @Schema(description = "收货人电话")
    private String receiverPhone;

    @Schema(description = "收货地址")
    private String receiverAddress;

    @Valid
    @NotEmpty
    @Schema(description = "下单明细（至少一项）", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<OrderItemDto> items;

    @Schema(description = "买家备注")
    private String remark;

    @Data
    @Schema(description = "下单商品明细 DTO")
    public static class OrderItemDto {

        @NotNull
        @Schema(description = "商品ID", requiredMode = Schema.RequiredMode.REQUIRED)
        private Long productId;

        @Schema(description = "规格ID（商品有规格则必传；无规格可为null）")
        private Long specId;

        @NotNull
        @Positive
        @Schema(description = "购买数量", requiredMode = Schema.RequiredMode.REQUIRED)
        private Integer num;
    }
}