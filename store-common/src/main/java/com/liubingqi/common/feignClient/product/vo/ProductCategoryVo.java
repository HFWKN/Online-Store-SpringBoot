package com.liubingqi.common.feignClient.product.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductCategoryVo {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "状态 (1:启用, 0:禁用)")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "修改时间")
    private LocalDateTime updateTime;

}
