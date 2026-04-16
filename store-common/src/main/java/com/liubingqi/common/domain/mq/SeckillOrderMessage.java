package com.liubingqi.common.domain.mq;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 秒杀下单消息体
 */
@Data
public class SeckillOrderMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 消息唯一标识，建议用 UUID
     */
    private String messageId;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 秒杀活动 ID
     */
    private Long activityId;

    /**
     * 下单明细（可扩展多商品）
     */
    private List<OrderItem> items;

    /**
     * 收货地址 ID
     */
    private Long addressId;

    /**
     * 实付金额
     */
    private BigDecimal payAmount;

    /**
     * 买家备注
     */
    private String remark;

    /**
     * 下单时间戳（毫秒）
     */
    private Long timestamp;


    /**
     *  订单明细
     */
    @Data
    public static class OrderItem implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 商品 ID
         */
        private Long productId;

        /**
         * 规格 ID
         */
        private Long specId;

        /**
         * 购买数量
         */
        private Integer num;
    }
}
