package com.liubingqi.common.feignClient.product;


import com.liubingqi.common.domain.Result;
import com.liubingqi.common.feignClient.product.vo.ProductCategoryVo;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 *  商品分类服务的远程调用类
 */
//@FeignClient(name = "store-product", path = "/product/category")
public interface ProductCategoryFeignClient {


    /**
     *  根据id批量查询分类信息
     * @param ids
     * @return
     */
    @PostMapping("/listById")
    Result<List<ProductCategoryVo>> listById(List<Long> ids);
}
