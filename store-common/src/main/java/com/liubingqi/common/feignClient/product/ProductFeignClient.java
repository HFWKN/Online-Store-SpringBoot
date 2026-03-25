package com.liubingqi.common.feignClient.product;


import com.liubingqi.common.domain.Result;
import com.liubingqi.common.feignClient.product.vo.ProductSpecVo;
import com.liubingqi.common.feignClient.product.vo.ProductVo;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 *  商品服务的远程调用类
 */
@FeignClient(name = "store-product", path = "/product")
public interface ProductFeignClient {


    /**
     *  根据商品id查询商品信息
     * @param productIds
     * @return
     */
    @PostMapping("/getByIds")
    Result<List<ProductVo>> getByIds(@RequestBody List<Long> productIds);

/*    *//**
     *  根据商品id查询规格信息
     *//*
    @GetMapping("/{productId}")
    @Operation(summary = "根据商品id查询该商品的全部规格")
    Result<List<ProductSpecVo>> selectAll(@PathVariable Long productId);*/


    /**
     *  批量查询商品规格
     * @param productIds
     * @return
     */
    @PostMapping("/spec/productIds")
    @Operation(summary = "批量查询商品规格")
    Result<List<Map<Long,ProductSpecVo>>> selectByIds(@RequestBody List<Long> productIds);
}
