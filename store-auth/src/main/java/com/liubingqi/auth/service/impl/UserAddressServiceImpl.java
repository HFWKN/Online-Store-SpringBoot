package com.liubingqi.auth.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.liubingqi.auth.domain.dto.UserAddressDto;
import com.liubingqi.auth.domain.po.UserAddress;
import com.liubingqi.auth.domain.vo.UserAddressVo;
import com.liubingqi.auth.mapper.UserAddressMapper;
import com.liubingqi.auth.service.IUserAddressService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liubingqi.common.exception.BusinessException;
import com.liubingqi.common.utils.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 用户地址表 服务实现类
 * </p>
 *
 * @author lbq
 * @since 2026-03-22
 */
@Service
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements IUserAddressService {


    /**
     *  修改默认收获地址
     * @param addressId
     */
    @Override
    @Transactional
    public void updateDefaultAddress(Long addressId) {
        if(addressId == null){
            throw new BusinessException("无数据");
        }
        // 获取用户id
        Long userId = UserContext.getUserId();

        // 查询该用户是否有默认地址
        UserAddress address = lambdaQuery()
                .eq(UserAddress::getUserid, userId)
                .eq(UserAddress::getIsDefault, 1)
                .eq(UserAddress::getIsDelete, 0)
                .one();

        // 如果有默认地址了
        if(address != null){
            // 取消掉
            boolean update = lambdaUpdate()
                    .eq(UserAddress::getId, address.getId())
                    .set(UserAddress::getIsDefault, 0)
                    .update();
            if(!update){
                throw new BusinessException("修改默认地址出错");
            }
        }
        // 将新地址选为默认地址
        boolean update1 = lambdaUpdate()
                .eq(UserAddress::getId, addressId)
                .eq(UserAddress::getUserid, userId)
                .set(UserAddress::getIsDefault, 1)
                .update();
        if(!update1){
            throw new BusinessException("修改默认地址出错");
        }
    }


    /**
     *  查询该用户的收货地址
     * @param userId
     * @return
     */
    @Override
    public List<UserAddressVo> selectAllAddress(Long userId) {
        if(userId == null){
            throw new BusinessException("未接收用户id");
        }
        List<UserAddress> list = lambdaQuery()
                .eq(UserAddress::getUserid, userId)
                .eq(UserAddress::getIsDelete, 0)
                .list();

        if(CollectionUtil.isEmpty(list)){
            // 列表为空, 返回空集合
            return new ArrayList<>();
        }
        List<UserAddressVo> voList = new ArrayList<>(list.size());
        for (UserAddress r : list) {
            UserAddressVo vo = new UserAddressVo();
            BeanUtil.copyProperties(r, vo);
            voList.add(vo);
        }
        return voList;
    }


    /**
     *  删除收货地址
     * @param id
     */
    @Override
    public void deleteAddress(Long id) {
        Long userId = UserContext.getUserId();
        boolean update = lambdaUpdate()
                .eq(UserAddress::getId, id)
                .eq(UserAddress::getUserid, userId)
                .set(UserAddress::getIsDelete, 1)
                .update();
        if(!update){
            throw new BusinessException("删除收货地址出错");
        }
    }
}
