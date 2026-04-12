package com.liubingqi.seckill.domain.dto;

import lombok.Data;

@Data
public class StockDto {

    private Long productId;
    private String productName;
    private Long activityId;
    private String imageUrl ;
    private Long stockId;
    private Long specId;
}
