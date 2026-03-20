package com.liubingqi.product.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品详情VO
 */
@Data
@Schema(description = "商品详情")
public class ProductVo {

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

    @Schema(description = "分类name")
    private Long categoryName;

    @Schema(description = "上架状态: 0-下架, 1-上架")
    private Integer status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "所属公司id")
    private String companyId;

    @Schema(description = "所属公司name")
    private String companyName;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
