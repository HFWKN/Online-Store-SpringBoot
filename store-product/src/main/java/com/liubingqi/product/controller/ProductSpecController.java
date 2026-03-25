package com.liubingqi.product.controller;


import com.liubingqi.common.domain.Result;
import com.liubingqi.product.domain.vo.ProductSpecVo;
import com.liubingqi.product.service.IProductSpecService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品规格表 前端控制器
 * </p>
 *
 * @author lbq
 * @since 2026-03-23
 */
@RestController
@RequestMapping("/product/spec")
@RequiredArgsConstructor
@Tag(name = "商品规格管理", description = "")
public class ProductSpecController {


    private final IProductSpecService productSpecService;


    /**
     *  查询该商品的全部规格
     * @param productId
     * @return
     */
    @GetMapping("/{productId}")
    @Operation(summary = "查询该商品的全部规格")
    public Result<List<ProductSpecVo>> selectAll(@PathVariable Long productId){
        List<ProductSpecVo> voList = productSpecService.selectAll(productId);
        return Result.success(voList);
    }


    /**
     *  批量查询商品规格
     * @param productIds
     * @return
     */
    @PostMapping("/productIds")
    @Operation(summary = "批量查询商品规格")
    public Result<List<Map<Long,ProductSpecVo>>> selectByIds(@RequestBody List<Long> productIds){
        List<Map<Long,ProductSpecVo>> list = productSpecService.selectByIds(productIds);
        return Result.success(list);
    }
}
