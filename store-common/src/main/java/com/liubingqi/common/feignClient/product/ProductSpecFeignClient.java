package com.liubingqi.common.feignClient.product;


import com.liubingqi.common.domain.Result;
import com.liubingqi.common.feignClient.product.vo.ProductSpecVo;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 *  商品规格服务的远程调用类
 */
@FeignClient(name = "store-product", path = "/product/spec")
public interface ProductSpecFeignClient {



    /**
     *  根据规格ids查询商品规格
     * @param specIds
     * @return
     */
    @PostMapping("/getBySpecIds")
    Result<List<ProductSpecVo>> getBySpecIds(@RequestBody List<Long> specIds);
}
