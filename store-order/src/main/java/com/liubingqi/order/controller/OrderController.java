package com.liubingqi.order.controller;


import com.liubingqi.common.domain.Result;
import com.liubingqi.common.exception.BusinessException;
import com.liubingqi.order.domain.dto.CreateOrderDto;
import com.liubingqi.order.service.IOrderItemService;
import com.liubingqi.order.service.IOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
@Slf4j
@RequestMapping("/order")
@Tag(name = "订单服务", description = "")
public class OrderController {

    private final IOrderService orderService;
    private final IOrderItemService orderItemService;



    @PostMapping("/getToken")
    @Operation(summary = "给前端发送订单token")
    public Result<String> getToken() {
        return Result.success(orderService.getToken());
    }

    /**
     *  用户下单-通过mq通知商品服务为某商品增加出售数并减少库存
     * @param dto
     * @return
     */
    @PostMapping("/userPlaceAnOrder")
    @Operation(summary = "用户下单")
    // 防止同一订单被重复提交，前端请求头传递此订单唯一标识token
    public Result<String> userPlaceAnOrder(@RequestHeader("X-Order-Token") String orderToken,
                                           @Valid @RequestBody CreateOrderDto dto){
        if (orderToken == null || orderToken.isBlank()) {
            log.error("无下单token标识");
            throw new BusinessException("此次为非法下单");
        }
      /*  // 判断dto非空
        List<CreateOrderDto.OrderItemDto> items = dto.getItems();
        if(items.)*/
        String msg = orderService.userPlaceAnOrder(dto, orderToken);
        return Result.success(msg);
    }
}
