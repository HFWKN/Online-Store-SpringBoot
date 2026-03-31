package com.liubingqi.order.controller;


import com.liubingqi.common.domain.Result;
import com.liubingqi.order.domain.dto.CreateOrderDto;
import com.liubingqi.order.domain.dto.UserOrderDto;
import com.liubingqi.order.service.IOrderItemService;
import com.liubingqi.order.service.IOrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 订单主表 前端控制器
 * </p>
 *
 * @author lbq
 * @since 2026-03-17
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
@Tag(name = "订单服务", description = "")
public class OrderController {

    private final IOrderService orderService;
    private final IOrderItemService orderItemService;


    /**
     *  用户下单-通过mq通知商品服务为某商品增加出售数并减少库存
     * @param dto
     * @return
     */
    @PostMapping("/userPlaceAnOrder")
    public Result<String> pay(@RequestBody CreateOrderDto dto){
        String msg = orderService.pay(dto);
        return Result.success(msg);
    }
}
