package com.liubingqi.cart.controller;


import com.liubingqi.cart.domain.dto.CartDto;
import com.liubingqi.cart.service.ICartService;
import com.liubingqi.common.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

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

}
