package com.liubingqi.product.controller;


import cn.hutool.core.collection.CollectionUtil;
import com.liubingqi.common.domain.PageQuery;
import com.liubingqi.common.domain.PageResult;
import com.liubingqi.common.domain.Result;
import com.liubingqi.product.domain.page.PageQueryByProduct;
import com.liubingqi.product.domain.po.Product;
import com.liubingqi.product.domain.vo.ProductSimpleVo;
import com.liubingqi.product.domain.vo.ProductVo;
import com.liubingqi.product.domain.vo.ProductWithCommentVo;
import com.liubingqi.product.service.IProductService;
import com.liubingqi.product.service.impl.ProductServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 商品基本信息表 前端控制器
 * </p>
 *
 *  用户端：商品的分页，商品的详细页，
 * @author lbq
 * @since 2026-03-17
 */
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
@Tag(name = "商品服务", description = "查询等")
public class ProductController {

    private final IProductService productService;


    /**
     *  商品列表分页查询
     * @param pageQuery
     * @return
     */
    @GetMapping("/page")
    @Operation(summary = "条件分页查询商品")
    public Result<PageResult<ProductSimpleVo>> pageList(PageQueryByProduct pageQuery){
        PageResult<ProductSimpleVo> list = productService.pageList(pageQuery);
        return Result.success(list);
    }

/*    *//**
     *  查询商品详情页
     * @param productId
     * @return
     *//*
    @GetMapping("/detailed/{productId}")
    @Operation(summary = "商品详情")
    public Result<ProductWithCommentVo> detailed(@PathVariable Integer productId){
        ProductWithCommentVo vo = productService.detailed(productId);
        return Result.success(vo);
    }*/
    /**
     *  远程调用 - 根据商品id批量获取商品信息（简略）
     */
    @PostMapping("/getByIds")
    public Result<List<ProductVo>> getByIds(@RequestBody List<Long> productIds){
        List<ProductVo> volist = productService.getByIds(productIds);
        return Result.success(volist);
    }

    /**
     *  远程调用，根据商品name模糊查询商品id
     */
    @GetMapping("/getByName/{name}")
    public Result<List<Long>> getByName(@PathVariable String name){
        List<Product> list = productService.lambdaQuery()
                .like(Product::getName, name)
                .list();

        // 获取商品id
        List<Long> productIds = list.stream()
                .map(Product::getId)
                .toList();

        if(CollectionUtil.isEmpty(productIds)){
            return Result.success();
        }
        return Result.success(productIds);
    }
}
