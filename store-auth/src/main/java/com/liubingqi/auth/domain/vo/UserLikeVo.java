package com.liubingqi.auth.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserLikeVo {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     *  商品name
     */
    private String productName;

    /**
     *  主图URL
     */
    private String mainImage;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     *  分类name
     */
    private String categoryName;

    /**
     *  价格
     */
    private BigDecimal price;

    /**
     *  商品规格id
     */
    private Long specId;

    /**
     *  商品颜色
     */
    private String color;

    /**
     *  商品规格
     */
    private String productSpec;
}
