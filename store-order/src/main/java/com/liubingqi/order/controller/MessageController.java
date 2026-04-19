package com.liubingqi.order.controller;

import com.liubingqi.common.domain.Result;
import com.liubingqi.common.domain.mq.SeckillOrderMessage;
import com.liubingqi.order.domain.vo.StoreMessageVo;
import com.liubingqi.order.service.IMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/order/message")
@Tag(name = "订单服务-通知服务", description = "")
public class MessageController {

    private final IMessageService messageService;


    /**
     *  给前端发送订单的消息
     * @param message
     * @return
     */
    @PostMapping("/send")
    @Operation(summary = "给前端发送订单的消息")
    public Result<Void> sendMessage(@RequestBody SeckillOrderMessage message){
        return messageService.saveMessage(message);
    }

    /**
     *  根据用户id查询订单状态消息
     * @return
     */
    @GetMapping("/getByUserId")
    @Operation(summary = "根据用户id增量查询订单状态消息")
    public Result<List<StoreMessageVo>> getByUserId(
            @RequestParam(value = "lastTime", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastTime,
            @RequestParam(value = "lastId", required = false) Long lastId,
            @RequestParam(value = "limit", required = false) Integer limit){
        return messageService.getByUserId(lastTime, lastId, limit);
    }


    /**
     *  获取所有消息
     * @return
     */
    @GetMapping("/getAll")
    @Operation(summary = "获取所有消息")
    public Result<List<StoreMessageVo>> getAll(){
        return messageService.getAll();
    }
}
