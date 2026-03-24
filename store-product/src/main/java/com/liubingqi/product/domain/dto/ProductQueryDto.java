package com.liubingqi.product.domain.dto;

import com.liubingqi.common.domain.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 商品查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "商品查询条件")
public class ProductQueryDto extends PageQuery {

    @Schema(description = "商品名称（模糊查询）", example = "iPhone")
    private String name;

    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    @Schema(description = "上架状态: 0-下架, 1-上架", example = "1")
    private Integer status;

    @Schema(description = "最低价格", example = "1000.00")
    private BigDecimal minPrice;

    @Schema(description = "最高价格", example = "10000.00")
    private BigDecimal maxPrice;

    @Schema(description = "所属公司id", example = "COMP001")
    private String companyId;
}
