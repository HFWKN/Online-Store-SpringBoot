package com.liubingqi.seckill.controller;


import com.liubingqi.common.domain.Result;
import com.liubingqi.seckill.domain.vo.StockVo;
import com.liubingqi.seckill.service.IStockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 商品库存表(秒杀核心) 前端控制器
 * </p>
 *
 * @author lbq
 * @since 2026-03-17
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/seckill")
@Tag(name = "秒杀服务-规格信息(库存)", description = "")
public class StockController {

    private final IStockService stockService;


    /**
     *  查询活动商品的规格信息
     * @param productId
     * @return
     */
    @GetMapping("/getStockSpecList/{productId}")
    @Operation(summary = "查询活动商品的规格信息")
    public Result<List<StockVo>> list(@PathVariable Long productId){
        return stockService.list(productId);
    }

}
