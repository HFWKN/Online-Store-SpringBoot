package com.liubingqi.product.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品创建/更新DTO
 */
@Data
@Schema(description = "商品创建/更新请求")
public class ProductDto {

    @NotBlank(message = "商品名称不能为空")
    @Schema(description = "商品名称", example = "iPhone 15 Pro")
    private String name;

    @Schema(description = "副标题/卖点", example = "全新A17 Pro芯片，钛金属设计")
    private String subTitle;

    @Schema(description = "主图URL", example = "https://example.com/images/iphone15.jpg")
    private String mainImage;

    @Schema(description = "商品详情HTML")
    private String detailHtml;

    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    @Schema(description = "原价", example = "7999.00")
    private BigDecimal price;

    @NotNull(message = "分类ID不能为空")
    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    @Schema(description = "上架状态: 0-下架, 1-上架", example = "1")
    private Integer status;

    @Schema(description = "所属公司id", example = "COMP001")
    private String company_id;

    @Schema(description = "所属公司name", example = "苹果公司")
    private String company_name;
}
