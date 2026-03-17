package com.liubingqi.seckill.domain.po;

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
 * 商品库存表(秒杀核心)
 * </p>
 *
 * @author lbq
 * @since 2026-03-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("store_stock")
@Schema(name = "Stock对象", description = "商品库存表(秒杀核心)")
public class Stock implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "库存ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "关联商品ID")
    private Long productId;

    @Schema(description = "总库存")
    private Integer totalStock;

    @Schema(description = "可用库存")
    private Integer availableStock;

    @Schema(description = "锁定库存")
    private Integer lockStock;

    @Schema(description = "版本号(乐观锁)")
    private Integer version;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


}
