package com.liubingqi.cart.service;

import com.liubingqi.cart.domain.dto.CartDto;
import com.liubingqi.cart.domain.po.Cart;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liubingqi.cart.domain.vo.CartVo;

import java.util.List;

/**
 * <p>
 * 购物车表 服务类
 * </p>
 *
 * @author lbq
 * @since 2026-03-17
 */
public interface ICartService extends IService<Cart> {

    // 添加商品到购物车
    void addCart(CartDto cartDto);

    // 查看我的购物车
    List<CartVo> selectAllByCart();
}
