package com.liubingqi.auth.domain.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 *  新增收藏dto
 */
@Data
public class UserLikeDto {

    /**
     *  商品id
     */
    private Long productId;

    /**
     *  分类id
     */
    private Long categoryId;

    /**
     *  规格id
     */
    private Long specId;
}
