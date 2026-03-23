package com.liubingqi.auth.service;

import com.liubingqi.auth.domain.dto.UserAddressDto;
import com.liubingqi.auth.domain.po.UserAddress;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liubingqi.auth.domain.vo.UserAddressVo;

import java.util.List;

/**
 * <p>
 * 用户地址表 服务类
 * </p>
 *
 * @author lbq
 * @since 2026-03-22
 */
public interface IUserAddressService extends IService<UserAddress> {

    // 修改默认收获地址
    void updateDefaultAddress(Long addressId);

    // 查询该用户的收货地址
    List<UserAddressVo> selectAllAddress(Long userId);

    // 删除收获地址
    void deleteAddress(Long id);
}
