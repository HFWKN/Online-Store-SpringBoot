package com.liubingqi.product.domain.po;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 商品基本信息表
 * </p>
 *
 * @author lbq
 * @since 2026-03-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("store_product")
@Schema(name = "Product对象", description = "商品基本信息表")
public class Product implements Serializable {

    // 默认序列化版本号
    private static final long serialVersionUID = 1L;

    @Schema(description = "商品ID")
    @TableId(value = "id", type = IdType.AUTO)
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

    @Schema(description = "上架状态: 0-下架, 1-上架")
    private Integer status;

    @Schema(description = "所属公司id")
    private String companyId;

    @Schema(description = "所属公司name")
    private String companyName;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
