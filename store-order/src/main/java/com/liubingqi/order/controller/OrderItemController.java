package com.liubingqi.order.controller;


import com.liubingqi.common.domain.Result;
import com.liubingqi.order.domain.vo.OrderVo;
import com.liubingqi.order.service.IOrderItemService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 订单明细表 前端控制器
 * </p>
 *
 * @author lbq
 * @since 2026-03-17
 */
@RestController
@RequestMapping("order/orderItem")
@RequiredArgsConstructor
public class OrderItemController {

        private final IOrderItemService orderItemService;


     /**
     *  查询当前用户的所有订单
     * @return
      */
     @GetMapping
     @Operation(summary = "查询当前用户订单明细")
     public Result<List<OrderVo>> selectAll(
             @RequestParam(required = false) Integer status,
             @RequestParam(required = false) String productName) {
         List<OrderVo> voList = orderItemService.selectAll(status, productName);
         return Result.success(voList);
     }
}
