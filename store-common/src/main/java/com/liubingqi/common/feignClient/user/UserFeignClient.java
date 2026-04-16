package com.liubingqi.common.feignClient.user;

import com.liubingqi.common.domain.Result;
import com.liubingqi.common.feignClient.user.vo.UserAddressVo;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


/**
 *  用户服务的远程调用类
 */
@FeignClient(name = "store-auth", path = "/user")
public interface UserFeignClient {


    /**
     *  根据地址id查询地址信息
     * @param addressId
     * @return
     */
    @GetMapping("/address/selectByAddressId/{addressId}")
    @Operation(summary = "根据地址id查询地址")
    Result<UserAddressVo> selectByAddressId(@PathVariable Long addressId);

    /**
     * 根据地址id和用户id查询地址信息（MQ/服务间调用推荐）
     */
    @GetMapping("/address/selectByAddressId/{addressId}/byUser")
    @Operation(summary = "根据地址id和用户id查询地址")
    Result<UserAddressVo> selectByAddressIdAndUserId(@PathVariable Long addressId, @RequestParam Long userId);
}
