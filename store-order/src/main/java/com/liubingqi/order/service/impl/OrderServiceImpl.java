package com.liubingqi.order.service.impl;

import com.liubingqi.order.domain.po.Order;
import com.liubingqi.order.mapper.OrderMapper;
import com.liubingqi.order.service.IOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单主表 服务实现类
 * </p>
 *
 * @author lbq
 * @since 2026-03-17
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

}
