package com.liubingqi.order.service.impl;

import com.liubingqi.order.domain.po.OrderItem;
import com.liubingqi.order.mapper.OrderItemMapper;
import com.liubingqi.order.service.IOrderItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单明细表 服务实现类
 * </p>
 *
 * @author lbq
 * @since 2026-03-17
 */
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements IOrderItemService {

}
