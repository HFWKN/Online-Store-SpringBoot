package com.liubingqi.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liubingqi.common.domain.Result;
import com.liubingqi.common.domain.mq.SeckillOrderMessage;
import com.liubingqi.order.domain.po.StoreMessage;
import com.liubingqi.order.domain.vo.StoreMessageVo;

import java.time.LocalDateTime;
import java.util.List;

public interface IMessageService extends IService<StoreMessage> {

    // 写入订单通知消息
    Result<Void> saveMessage(SeckillOrderMessage message);

    // 根据用户id增量查询订单状态消息
    Result<List<StoreMessageVo>> getByUserId(LocalDateTime lastTime, Long lastId, Integer limit);

    // 查询所有消息
    Result<List<StoreMessageVo>> getAll();

}
