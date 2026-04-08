package com.liubingqi.seckill.domain.po;

import java.math.BigDecimal;
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
 * 秒杀商品信息表（列表/详情基础信息）
 * </p>
 *
 * @author lbq
 * @since 2026-04-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("store_stock_product")
public class StockProduct implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 秒杀活动ID
     */
    private Long activityId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品名称快照
     */
    private String productName;

    /**
     * 商品主图快照
     */
    private String mainImage;

    /**
     * 原价快照
     */
    private BigDecimal originPrice;

    /**
     * 状态: 0未开始 1进行中 2已结束 3下架
     */
    private Integer status;

    /**
     * 列表排序，越小越靠前
     */
    private Integer sortNo;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
