package com.liubingqi.product.controller;


import com.liubingqi.common.domain.Result;
import com.liubingqi.product.domain.vo.ProductSpecVo;
import com.liubingqi.product.service.IProductSpecService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
public class ProductSpecController {


    private final IProductSpecService productSpecService;


    /**
     *  查询该商品的全部规格
     * @param productId
     * @return
     */
    @GetMapping("/{productId}")
    public Result<List<ProductSpecVo>> selectAll(@PathVariable Long productId){
        List<ProductSpecVo> voList = productSpecService.selectAll(productId);
        return Result.success(voList);
    }
}
