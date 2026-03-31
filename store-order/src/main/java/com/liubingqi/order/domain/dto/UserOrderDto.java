package com.liubingqi.order.domain.dto;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 *  用户下单dto
 */
@Data
public class UserOrderDto {

    /**
     *  用户id
     */
    private Long userId;

    /**
     *  商品id集合
     */
    private List<Long> productIds;

    /**
     *  订单总金额
     */
    private BigDecimal totalAmount;

    /**
     *  实付金额
     */
    private BigDecimal payAmount;

    /**
     * 订单状态
     */
    private Integer status;

    /**
     *  支付时间
     */
    private LocalDateTime paymentTime;

    /**
     *  发货时间
     */
    private LocalDateTime deliveryTime;

    /**
     *  收货人name
     */
    private String receiverName;

    /**
     *  收货电话
     */
    private String receiverPhone;

    /**
     *  收货地址
     */
    private String receiverAddress;
}
