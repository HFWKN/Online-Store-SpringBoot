package com.liubingqi.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liubingqi.common.domain.Result;
import com.liubingqi.common.domain.mq.SeckillOrderMessage;
import com.liubingqi.order.domain.po.StoreMessage;
import com.liubingqi.order.domain.vo.StoreMessageVo;

import java.util.List;

public interface IMessageService extends IService<StoreMessage> {

    // 给前端发送订单的消息
    Result<String> sendMessage(SeckillOrderMessage message);

    // 根据用户id查询订单状态消息
    Result<List<StoreMessageVo>> getByUserId();
}
