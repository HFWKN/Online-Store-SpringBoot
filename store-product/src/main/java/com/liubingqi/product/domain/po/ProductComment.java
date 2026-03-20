package com.liubingqi.product.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 商品评价表
 * </p>
 *
 * @author lbq
 * @since 2026-03-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("store_product_comment")
public class ProductComment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
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
     * 商品名称快照 (冗余字段)
     */
    private String productName;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称快照 (冗余字段)
     */
    private String categoryName;

    /**
     * 购买时间 (用于验证是否已购买)
     */
    private LocalDateTime buyTime;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 评分 (1-5星)
     */
    private Integer starRating;

    /**
     *  是否隐藏（1隐藏  0未隐藏）
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;


}
