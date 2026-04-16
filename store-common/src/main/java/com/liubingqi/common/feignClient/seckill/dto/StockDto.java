package com.liubingqi.common.feignClient.seckill.dto;

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