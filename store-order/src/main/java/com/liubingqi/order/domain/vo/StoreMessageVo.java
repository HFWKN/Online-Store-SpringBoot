package com.liubingqi.order.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StoreMessageVo {

    @Schema(description = "消息ID")
    private Long id;

    @Schema(description = "商品名称")
    private String productName;

    @Schema(description = "规格名称")
    private String specName;

    @Schema(description = "通知内容")
    private String content;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
