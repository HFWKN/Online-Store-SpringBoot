package com.liubingqi.product.controller;


import com.liubingqi.common.domain.Result;
import com.liubingqi.product.domain.po.ProductComment;
import com.liubingqi.product.service.IProductCommentService;
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
 * 商品评价表 前端控制器
 * </p>
 *
 * @author lbq
 * @since 2026-03-20
 */
@RestController
@RequestMapping("/product-comment")
@RequiredArgsConstructor
@Tag(name = "商品评价管理", description = "商品评价相关接口")
public class ProductCommentController {

    private final IProductCommentService productCommentService;


    /**
     *  根据商品id查询其评论集合
     * @param productId
     * @return
     */
    @GetMapping("/comment/{productId}")
    @Operation(summary = "根据商品id查询其评论集合")
    public Result<List<ProductComment>> getCommentById(@PathVariable Integer productId){
        return Result.success(productCommentService.getCommentById(productId));
    }

    /**
     *  好评率
     * @param productId
     * @return
     */
    @GetMapping("/goodCommentRate/{productId}")
    @Operation(summary = "查询好评率")
    public Result<Integer> goodCommentRate(@PathVariable Integer productId){
        return Result.success(productCommentService.goodCommentRate(productId));
    }

    /**
     *  获取好评TOP10
     * @return
     */
    @GetMapping("/goodCommentTOP10")
    @Operation(summary = "好评TOP10")
    public Result<List<ProductComment>> goodCommentTOP10(){
        List<ProductComment> top10 = productCommentService.goodCommentTOP10();
        return Result.success(top10);
    }

}
