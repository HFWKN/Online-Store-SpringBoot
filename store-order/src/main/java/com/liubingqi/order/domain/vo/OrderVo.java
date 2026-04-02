package com.liubingqi.order.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 *  我的订单返回VO
 */
@Data
public class OrderVo {

    /**
     *  订单明细id
     */
    private Long id;

    /**
     *  用户id
     */
    private Long userId;

    /**
     *  订单明细状态
     */
    private Integer status;

    /**
     *  订单明细编号
     */
    private String orderItemNo;

    /**
     *  商品ID
     */
    private Long productId;

    /**
     *  商品图片url
     */
    private String mainImage;

    /**
     *  商品名称快照
     */
    private String productName;

    /**
     *  商品规格id
     */
    private Long specId;

    /**
     *  商品规格
     */
    private String productSpec;

    /**
     *  颜色
     */
    private String color;

    /**
     *  购买单价快照
     */
    private BigDecimal productPrice;

    /**
     *  数量
     */
    private Integer quantity;

    /**
     *  小计金额
     */
    private BigDecimal totalPrice;
}
