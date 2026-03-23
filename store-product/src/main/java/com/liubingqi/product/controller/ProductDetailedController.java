package com.liubingqi.product.controller;


import com.liubingqi.common.domain.Result;
import com.liubingqi.product.domain.vo.ProductVo;
import com.liubingqi.product.domain.vo.ProductWithCommentVo;
import com.liubingqi.product.service.IProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product/detailed")
@RequiredArgsConstructor
@Tag(name = "商品详细服务", description = "商品的详细界面")
public class ProductDetailedController {


    private final IProductService productService;


    /**
     *  商品详情界面
     * @param productId
     * @return
     */
    @GetMapping("/{productId}")
    public Result<ProductWithCommentVo> detailed(Integer productId){
        ProductWithCommentVo detailed = productService.detailed(productId);
        return Result.success(detailed);
    }
}
