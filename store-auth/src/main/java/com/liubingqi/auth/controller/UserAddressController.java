package com.liubingqi.auth.controller;


import cn.hutool.core.bean.BeanUtil;
import com.liubingqi.auth.domain.dto.UserAddressDto;
import com.liubingqi.auth.domain.po.UserAddress;
import com.liubingqi.auth.domain.vo.UserAddressVo;
import com.liubingqi.auth.service.IUserAddressService;
import com.liubingqi.common.domain.Result;
import com.liubingqi.common.exception.BusinessException;
import com.liubingqi.common.utils.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 用户地址表 前端控制器
 * </p>
 *
 * @author lbq
 * @since 2026-03-22
 */
@RestController
@RequestMapping("/user/address")
@RequiredArgsConstructor
@Tag(name = "用户收货地址")
public class UserAddressController {

    private final IUserAddressService userAddressService;


    /**
     *  查询该用户的收货地址信息
     * @return
     */
    @GetMapping("/select")
    @Operation(summary = "查询该用户的收货地址")
    public Result<List<UserAddressVo>> selectAllAddress(){
        List<UserAddressVo> voList = userAddressService.selectAllAddress(UserContext.getUserId());
        return Result.success(voList);
    }

    /**
     *  新增收货地址信息
     * @param dto
     * dto里面只需要地址
     */
    @PostMapping("/add")
    @Operation(summary = "新增收货地址")
    public Result<Void> addAddress(@RequestBody UserAddressDto dto){
        UserAddress userAddress = new UserAddress();
        BeanUtil.copyProperties(dto,userAddress);
        // 将usercontext中的用户id设置到userAddress中
        userAddress.setUserid(UserContext.getUserId());
        userAddressService.save(userAddress);
        return Result.success();
    }


    /**
     *  修改默认收货地址信息
     * @param addressId
     * 前端传“要修改为默认地址的地址id”
     */
    @PutMapping("/updateDefault/{addressId}")
    @Operation(summary = "修改默认收获地址")
    public Result<Void> updateDefaultAddress(@PathVariable Long addressId){
       userAddressService.updateDefaultAddress(addressId);
       return Result.success();
    }

    /**
     *  删除收货地址信息(逻辑删除)
     * @param id
     * 后端需要地址id
     */
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除收获地址")
    public Result<Void> delete(@PathVariable Long id){
        if(id == null){
            throw new BusinessException("id不能为空");
        }
        userAddressService.deleteAddress(id);
        return Result.success();
    }


    /**
     *  修改收货地址信息
     * @param dto
     * @return
     * 前端需要传dto为{地址id，地址}
     */
    @PutMapping("/update")
    @Operation(summary = "修改收货地址")
    @Transactional
    public Result<Void> update(@RequestBody UserAddressDto dto){
        UserAddress userAddress = new UserAddress();
        BeanUtil.copyProperties(dto,userAddress);
        UserAddress address = userAddressService.getById(dto.getId());
        if(address.getIsDelete() == 1){
            throw new BusinessException("该地址已被删除");
        }
        boolean update = userAddressService.lambdaUpdate()
                .eq(UserAddress::getId, dto.getId())
                .eq(UserAddress::getUserid, UserContext.getUserId())
                .set(UserAddress::getAddress, dto.getAddress())
                .update();
        if(!update){
            throw new BusinessException("修改收货地址出错");
        }
        return Result.success();
    }


    /**
     *  根据地址id查询地址信息 --- 普通请求
     * @param addressId
     * @return
     */
    @GetMapping("/selectByAddressId/{addressId}")
    @Operation(summary = "根据地址id查询地址")
    public Result<UserAddressVo> selectByAddressId(@PathVariable Long addressId){
        // 普通请求从用户context中获取userId
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        return selectByAddressIdAndUserId(addressId, userId);
    }

    /**
     *  根据地址id和用户id查询地址信息（服务间调用）
     * @param addressId 地址id
     * @param userId 用户id
     * @return 地址信息
     */
    @GetMapping("/selectByAddressId/{addressId}/byUser")
    @Operation(summary = "根据地址id和用户id查询地址")
    public Result<UserAddressVo> selectByAddressIdAndUserId(@PathVariable Long addressId,
                                                             @RequestParam Long userId){
        UserAddress address = userAddressService.lambdaQuery()
                .eq(UserAddress::getId, addressId)
                .eq(UserAddress::getUserid, userId)
                .eq(UserAddress::getIsDelete, 0)
                .one();
        if (address == null) {
            throw new BusinessException("收货地址不存在或无权限访问");
        }
        UserAddressVo vo = new UserAddressVo();
        BeanUtil.copyProperties(address,vo);
        return Result.success(vo);
    }
}
