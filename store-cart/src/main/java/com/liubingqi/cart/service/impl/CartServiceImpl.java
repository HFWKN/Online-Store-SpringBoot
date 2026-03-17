package com.liubingqi.cart.service.impl;

import com.liubingqi.cart.domain.po.Cart;
import com.liubingqi.cart.mapper.CartMapper;
import com.liubingqi.cart.service.ICartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
