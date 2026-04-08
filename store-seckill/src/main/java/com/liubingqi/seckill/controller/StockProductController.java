package com.liubingqi.seckill.controller;


import com.liubingqi.common.domain.Result;
import com.liubingqi.seckill.domain.vo.StockProductVo;
import com.liubingqi.seckill.service.IStockProductService;
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
 * 秒杀商品信息表（列表/详情基础信息） 前端控制器
 * </p>
 *
 * @author lbq
 * @since 2026-04-08
 */
@RestController
@RequestMapping("/seckill/product")
@RequiredArgsConstructor
@Tag(name = "秒杀服务-商品信息", description = "")
public class StockProductController {

    private final IStockProductService stockProductService;


    /**
     *  获取当前活动秒杀商品列表
     * @param activityId
     * @return
     */
    @GetMapping("/list/{activityId}")
    @Operation(summary = "查询参与改成活动的商品")
    public Result<List<StockProductVo>> list(@PathVariable Long activityId){
        return stockProductService.list(activityId);
    }
}
