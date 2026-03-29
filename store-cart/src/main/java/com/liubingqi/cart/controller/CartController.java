package com.liubingqi.cart.controller;


import com.liubingqi.cart.domain.dto.CartDto;
import com.liubingqi.cart.domain.vo.CartVo;
import com.liubingqi.cart.service.ICartService;
import com.liubingqi.common.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 购物车表 前端控制器
 * </p>
 *
 * @author lbq
 * @since 2026-03-17
 */
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@Tag(name = "购物车服务", description = "查询等")
public class CartController {


    private final ICartService cartService;


    /**
     *  添加商品到购物车
     * @param cartDto
     * @return
     */
    @PostMapping("/addCart")
    @Operation(summary = "添加商品到购物车")
    public Result<Void> addCart(@RequestBody CartDto cartDto){
        cartService.addCart(cartDto);
        return Result.success();
    }


    /**
     *
     *  查看我的购物车
     * @return
     */
    @PostMapping("/selectAllByCart")
    @Operation(summary = "查看我的购物车")
    public Result<List<CartVo>> selectAllByCart(){
        List<CartVo> voList = cartService.selectAllByCart();
        return Result.success(voList);
    }


    /**
     *  批量删除购物车中的商品
     * @param ids
     * @return
     */
    @PostMapping("/deleteCart")
    @Operation(summary = "批量删除购物车中的商品")
    public Result<Void> deleteCart(@RequestBody List<Long> ids){
        cartService.removeByIds(ids);
        return Result.success();
    }


    /**
     *  根据商品名称查询购物车
     * @param name
     * @return
     */
    @GetMapping("/selectByName")
    @Operation(summary = "根据商品名称查询购物车")
    public Result<List<Long >> selectByName(String name){
        /**
         *  1.根据商品名称查询
         *  2.返回商品id集合
         */
        List<Long> cartVos = cartService.selectByName(name);
        return Result.success(cartVos);
    }
}
