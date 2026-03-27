package com.liubingqi.cart.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.liubingqi.cart.domain.dto.CartDto;
import com.liubingqi.cart.domain.po.Cart;
import com.liubingqi.cart.domain.vo.CartVo;
import com.liubingqi.cart.mapper.CartMapper;
import com.liubingqi.cart.service.ICartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liubingqi.common.domain.Result;
import com.liubingqi.common.exception.BusinessException;
import com.liubingqi.common.feignClient.product.ProductCategoryFeignClient;
import com.liubingqi.common.feignClient.product.ProductFeignClient;
import com.liubingqi.common.feignClient.product.ProductSpecFeignClient;
import com.liubingqi.common.feignClient.product.vo.ProductCategoryVo;
import com.liubingqi.common.feignClient.product.vo.ProductSpecVo;
import com.liubingqi.common.feignClient.product.vo.ProductVo;
import com.liubingqi.common.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 购物车表 服务实现类
 * </p>
 *
 * @author lbq
 * @since 2026-03-17
 */
@Service
@RequiredArgsConstructor
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements ICartService {


    private final ProductFeignClient productFeignClient;
    //private final ProductCategoryFeignClient CategoryFeignClient;
    //private final ProductSpecFeignClient productSpecFeignClient;

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


    /**
     *  查看我的购物车
     * @return
     */
    @Override
    public List<CartVo> selectAllByCart() {
        Long userId = UserContext.getUserId();
        // 查询我的购物车
        List<Cart> list = lambdaQuery()
                .eq(Cart::getUserId, userId)
                .list();
        // 如果是空集合
        if(CollectionUtil.isEmpty(list)){
            // 返回空集合
            return new ArrayList<>();
        }
        // 创建map，里面存放购物车id 和 叠加的金额
        Map<Long, Double> priceMap = new HashMap<>();

        // 创建商品ids集合
        List<Long> productIds = new ArrayList<>();
        // 创建分类ids集合
        List<Long> categoryIds = new ArrayList<>();
        // 创建规格ids集合
        List<Long> specIds = new ArrayList<>();

        // 遍历商品信息集合，取出所需id
        for (Cart c : list) {
            productIds.add(c.getProductId());
            categoryIds.add(c.getCategoryId());
            specIds.add(c.getSpecId());
            priceMap.put(c.getId(), c.getPrice());
        }


        // 远程调用，获得商品信息
        Result<List<ProductVo>> productInfo = productFeignClient.getByIds(productIds);
        List<ProductVo> productList = productInfo.getData();
        if(CollectionUtil.isEmpty(productList)){
            return new ArrayList<>();
        }
        // 创建商品信息map
        Map<Long, ProductVo> productMap = new HashMap<>();
        for (ProductVo p : productList) {
            productMap.put(p.getId(), p);
        }

        // 远程调用，获取分类信息，并封装进map
        Result<List<ProductCategoryVo>> categoryInfo = productFeignClient.listById(categoryIds);
        List<ProductCategoryVo> categoryList = categoryInfo.getData();
        if(CollectionUtil.isEmpty(categoryList)){
            return new ArrayList<>();
        }
        Map<Long, ProductCategoryVo> categoryMap = new HashMap<>();
        for (ProductCategoryVo c : categoryList) {
            categoryMap.put(c.getId(), c);
        }

        // 远程调用，获取规格信息，并封装进map
        Result<List<ProductSpecVo>> specInfo = productFeignClient.getBySpecIds(specIds);
        List<ProductSpecVo> specList = specInfo.getData();
        if(CollectionUtil.isEmpty(specList)){
            return new ArrayList<>();
        }
        Map<Long, ProductSpecVo> specMap = new HashMap<>();
        for (ProductSpecVo s : specList) {
            specMap.put(Long.valueOf(s.getId()), s);
        }

        // 创建购物车信息集合
        List<CartVo> cartList = new ArrayList<>();
        for (Cart c : list) {
            CartVo cartVo = new CartVo();
            cartVo.setId(c.getId());
            cartVo.setUserId(userId);
            cartVo.setProductId(c.getProductId());
            cartVo.setProductName(productMap.get(c.getProductId()).getName());
            cartVo.setMainImage(productMap.get(c.getProductId()).getMainImage());
            cartVo.setCategoryId(c.getCategoryId());
            cartVo.setCategoryName(categoryMap.get(c.getCategoryId()).getName());
            cartVo.setSpecPrice(specMap.get(c.getSpecId()).getSpecPrice());
            cartVo.setProductSpec(specMap.get(c.getSpecId()).getSpec());
            cartList.add(cartVo);
        }
        return cartList;
    }
}
