package com.liubingqi.product.domain.page;

import com.liubingqi.common.domain.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 分页查询请求类 --- 商品/查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "商品查询条件")
public class PageQueryByProduct extends PageQuery {

    @Schema(description = "商品名称（模糊查询）", example = "")
    private String productName;

    @Schema(description = "分类ID", example = "")
    private Long categoryId;

/*    @Schema(description = "上架状态: 0-下架, 1-上架", example = "1")
    private Integer status;*/

    @Schema(description = "最低价格", example = "")
    private BigDecimal minPrice;

    @Schema(description = "最高价格", example = "999999")
    private BigDecimal maxPrice;

    // 按升序还是降序
    @Schema(description = "排序(1：升序/2：降序)", example = "")
    private Long sort;

/*    @Schema(description = "所属公司id", example = "COMP001")
    private String companyId;*/
}