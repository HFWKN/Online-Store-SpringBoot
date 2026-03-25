package com.liubingqi.cart.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.liubingqi.cart.domain.dto.CartDto;
import com.liubingqi.cart.domain.po.Cart;
import com.liubingqi.cart.mapper.CartMapper;
import com.liubingqi.cart.service.ICartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liubingqi.common.exception.BusinessException;
import com.liubingqi.common.utils.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 购物车表 服务实现类
 * </p>
 *
 * @author lbq
 * @since 2026-03-17
 */
@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements ICartService {


    /**
     *  添加商品到购物车
     * @param cartDto
     */
    @Override
    @Transactional
    public void addCart(CartDto cartDto) {
        if(cartDto == null){
            throw new BusinessException("商品信息为空");
        }
        // 如果商品id和用户id和规格id在数据库有一致的信息时，只增加数量
        Cart cart = lambdaQuery()
                .eq(Cart::getProductId, cartDto.getProductId())
                .eq(Cart::getUserId, UserContext.getUserId())
                .eq(Cart::getSpecId, cartDto.getSpecId())
                .one();
        // 并且将金额页同步增加
        Integer num = cartDto.getNum();
        Double price = cartDto.getPrice();
        Double newPrice = num * price;
        if(cart != null){
            // 修改数量
            cart.setNum(cart.getNum() + cartDto.getNum());
            // 修改金额
            cart.setPrice(newPrice + cart.getPrice());
            if (!updateById(cart)){
                throw new BusinessException("加入购物车错误");
            }
            return;
        }

        // 添加商品信息到购物车
        Cart cart1 = BeanUtil.copyProperties(cartDto, Cart.class);
        cart1.setUserId(UserContext.getUserId());
        // 设置总金额
        cart1.setPrice(newPrice);
        if (!save(cart1)){
            throw new BusinessException("加入购物车错误");
        }
    }
}
