package com.liubingqi.product.domain.vo;

import com.liubingqi.product.domain.po.ProductComment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品信息与评价VO
 *
 * @author lbq
 * @since 2026-03-20
 */
@Data
@Schema(name = "ProductWithCommentVo", description = "商品信息与评价视图对象")
public class ProductWithCommentVo {

    // ========== 商品基本信息 ==========
    
    @Schema(description = "商品ID")
    private Long id;

    @Schema(description = "商品名称")
    private String name;

    @Schema(description = "副标题/卖点")
    private String subTitle;

    @Schema(description = "主图URL")
    private String mainImage;

    @Schema(description = "商品详情HTML")
    private String detailHtml;

    @Schema(description = "原价")
    private BigDecimal price;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "上架状态: 0-下架, 1-上架")
    private Integer status;

    @Schema(description = "所属公司id")
    private String companyId;

    @Schema(description = "所属公司name")
    private String companyName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    // ========== 评价统计信息 ==========

    @Schema(description = "评价总数")
    private Long commentCount;

    @Schema(description = "平均评分")
    private BigDecimal averageRating;

    @Schema(description = "5星评价数")
    private Long fiveStarCount;

    @Schema(description = "4星评价数")
    private Long fourStarCount;

    @Schema(description = "3星评价数")
    private Long threeStarCount;

    @Schema(description = "2星评价数")
    private Long twoStarCount;

    @Schema(description = "1星评价数")
    private Long oneStarCount;

    @Schema(description = "好评率 (4-5星占比, %)")
    private Integer goodCommentRate;

    // ========== 评价列表 (可选) ==========

    @Schema(description = "最新评价列表,前两条")
    private List<ProductComment> latestComments;
    @Schema(description = "全部评价列表")
    private List<ProductComment> allComments;
}
