package com.liubingqi.order.service;

import com.liubingqi.order.domain.po.OrderItem;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liubingqi.order.domain.vo.OrderVo;

import java.util.List;

/**
 * <p>
 * 订单明细表 服务类
 * </p>
 *
 * @author lbq
 * @since 2026-03-17
 */
public interface IOrderItemService extends IService<OrderItem> {

    // 查询当前用户的所有订单
    List<OrderVo> selectAll(Integer status, String productName);
}
