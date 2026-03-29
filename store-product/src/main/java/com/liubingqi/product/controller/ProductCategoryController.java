package com.liubingqi.product.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.liubingqi.common.domain.Result;
import com.liubingqi.common.exception.BusinessException;
import com.liubingqi.product.domain.po.Product;
import com.liubingqi.product.domain.po.ProductCategory;
import com.liubingqi.product.domain.vo.ProductCategoryVo;
import com.liubingqi.product.service.IProductCategoryService;
import com.liubingqi.product.service.impl.ProductServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 商品分类表 前端控制器
 * </p>
 *
 * @author lbq
 * @since 2026-03-19
 */
@RestController
@RequestMapping("/product/category")
@RequiredArgsConstructor
@Tag(name = "商品分类", description = "展示分类")
public class ProductCategoryController {

    private final IProductCategoryService productCategoryService;


    @GetMapping("/list")
    @Operation(summary = "展示所有分类")
    public Result<List<ProductCategoryVo>> list(){
        List<ProductCategory> list = productCategoryService.list();
        List<ProductCategoryVo> voList = BeanUtil.copyToList(list, ProductCategoryVo.class);
        return Result.success(voList);
    }

    @PostMapping("/listById")
    @Operation(summary = "根据id批量查询分类信息")
    public Result<List<ProductCategoryVo>> listById(@RequestBody List<Long> ids){
        List<ProductCategory> categoryList = productCategoryService.lambdaQuery()
                .in(ProductCategory::getId, ids)
                .list();

        if(CollectionUtil.isEmpty(categoryList)){
            throw new BusinessException("没有分类信息");
        }

        List<ProductCategoryVo> voList = BeanUtil.copyToList(categoryList, ProductCategoryVo.class);
        return Result.success(voList);
    }

}
