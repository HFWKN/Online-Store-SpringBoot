package com.liubingqi.order.domain.po;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 订单明细表
 * </p>
 *
 * @author lbq
 * @since 2026-03-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("store_order_item")
@Schema(name = "OrderItem对象", description = "订单明细表")
public class OrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "关联订单ID")
    private Long orderId;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "订单明细编号")
    private String orderItemNo;

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "商品名称快照")
    private String productName;

    @Schema(description = "商品规格id")
    private Long specId;

    @Schema(description = "商品规格")
    private String productSpec;

    @Schema(description = "颜色")
    private String color;

    @Schema(description = "购买单价快照")
    private BigDecimal productPrice;

    @Schema(description = "数量")
    private Integer quantity;

    @Schema(description = "小计金额")
    private BigDecimal totalPrice;


}
