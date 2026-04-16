package com.liubingqi.order.service;

import com.liubingqi.common.domain.mq.SeckillOrderMessage;
import com.liubingqi.order.domain.dto.CreateOrderDto;
import com.liubingqi.order.domain.po.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liubingqi.order.domain.vo.OrderVo;

import java.util.List;

/**
 * <p>
 * 订单主表 服务类
 * </p>
 *
 * @author lbq
 * @since 2026-03-17
 */
public interface IOrderService extends IService<Order> {


    String getToken();

    // 用户下单
    String userPlaceAnOrder(CreateOrderDto dto, String orderToken);

    // 查询当前用户的所有订单
    //List<OrderVo> selectAll(Integer status);

    // 下单-MQ
    String createOrderFromMq(SeckillOrderMessage order);
}
