package com.liubingqi.seckill.constants;

import lombok.Data;

@Data
public class SpecNum {

    /**
     *  秒杀库存id
     */
    private Long stockId;
    /**
     *  商品id
     */
    private Long productId;
    /**
     *  规格id
     */
    private Long specId;
    /**
     *  规格数量
     */
    private Integer num;
}
