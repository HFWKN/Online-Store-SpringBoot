package com.liubingqi.cart.domain.dto;


import lombok.Data;


/**
 *  添加购物车dto
 */
@Data
public class CartDto {

    /**
     *  商品id
     */
    private Long productId;

    /**
     *  分类id
     */
    private Long categoryId;

    /**
     *  商品规格id
     */
    private Long specId;

    /**
     *  商品价格
     */
    private Double price;

    /**
     *  商品数量
     */
    private Integer num;

}
