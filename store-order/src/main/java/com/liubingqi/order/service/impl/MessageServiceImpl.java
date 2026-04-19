package com.liubingqi.order.service.impl;

import com.liubingqi.common.domain.Result;
import com.liubingqi.common.domain.mq.SeckillOrderMessage;
import com.liubingqi.common.feignClient.product.ProductFeignClient;
import com.liubingqi.common.feignClient.product.vo.ProductSpecVo;
import com.liubingqi.common.feignClient.product.vo.ProductVo;
import com.liubingqi.common.utils.UserContext;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liubingqi.order.domain.po.StoreMessage;
import com.liubingqi.order.domain.vo.StoreMessageVo;
import com.liubingqi.order.mapper.StoreMessageMapper;
import com.liubingqi.order.service.IMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl extends ServiceImpl<StoreMessageMapper, StoreMessage> implements IMessageService {

    private static final DateTimeFormatter MESSAGE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy.M.d.HH:mm:ss");

    private final ProductFeignClient productFeignClient;

    /**
     *  给前端发送订单的消息
     * @param message
     * @return
     */
    @Override
    public Result<Void> saveMessage(SeckillOrderMessage message) {
        if (message == null || message.getItems() == null || message.getItems().isEmpty()) {
            return Result.fail("消息体为空，无法生成通知");
        }

        SeckillOrderMessage.OrderItem item = message.getItems().get(0);
        if (item == null || item.getProductId() == null || item.getSpecId() == null) {
            return Result.fail("消息商品信息不完整，无法生成通知");
        }

        String productName = "商品";
        Result<List<ProductVo>> productResult = productFeignClient.getByIds(Collections.singletonList(item.getProductId()));
        if (productResult != null && productResult.getData() != null && !productResult.getData().isEmpty()) {
            ProductVo productVo = productResult.getData().get(0);
            if (productVo != null && productVo.getName() != null && !productVo.getName().isBlank()) {
                productName = productVo.getName();
            }
        }

        String specName = "默认规格";
        Result<List<ProductSpecVo>> specResult = productFeignClient.getBySpecIds(Collections.singletonList(item.getSpecId()));
        if (specResult != null && specResult.getData() != null && !specResult.getData().isEmpty()) {
            ProductSpecVo specVo = specResult.getData().get(0);
            if (specVo != null) {
                String color = specVo.getColor() == null ? "" : specVo.getColor().trim();
                String productSpec = specVo.getProductSpec() == null ? "" : specVo.getProductSpec().trim();
                if (!color.isEmpty() && !productSpec.isEmpty()) {
                    specName = color + "-" + productSpec;
                } else if (!productSpec.isEmpty()) {
                    specName = productSpec;
                } else if (!color.isEmpty()) {
                    specName = color;
                }
            }
        }

        String now = LocalDateTime.now().format(MESSAGE_TIME_FORMATTER);
        String content = "您在" + now + "下单的 " + productName + " [" + specName + "] 下单失败，请重试。";

        StoreMessage messageEntity = new StoreMessage();
        messageEntity.setUserId(message.getUserId());
        messageEntity.setProductName(productName);
        messageEntity.setSpecName(specName);
        messageEntity.setContent(content);
        messageEntity.setCreateTime(LocalDateTime.now());
        messageEntity.setUpdateTime(LocalDateTime.now());

        this.save(messageEntity);

        return Result.success();

    }


    /**
     *  根据用户id查询订单状态消息
     * @return
     */
    @Override
    public Result<List<StoreMessageVo>> getByUserId(LocalDateTime lastTime, Long lastId, Integer limit) {
        Long userId = UserContext.getUserId();
        if (userId == null){
            return Result.unauthorized();
        }
        // 策略B：首次不返回历史消息，前端应传当前时间作为游标后再轮询增量。
        if (lastTime == null) {
            return Result.success(Collections.emptyList());
        }

        int pageSize = normalizeLimit(limit);
        LambdaQueryWrapper<StoreMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StoreMessage::getUserId, userId);
        if (lastId != null && lastId > 0) {
            queryWrapper.and(w -> w.gt(StoreMessage::getCreateTime, lastTime)
                    .or(ow -> ow.eq(StoreMessage::getCreateTime, lastTime)
                            .gt(StoreMessage::getId, lastId)));
        } else {
            queryWrapper.gt(StoreMessage::getCreateTime, lastTime);
        }
        queryWrapper.orderByAsc(StoreMessage::getCreateTime)
                .orderByAsc(StoreMessage::getId)
                .last("LIMIT " + pageSize);
        List<StoreMessage> messageList = list(queryWrapper);

        if (messageList == null || messageList.isEmpty()) {
            return Result.success(Collections.emptyList());
        }

        ArrayList<StoreMessageVo> storeMessageVos = new ArrayList<>(messageList.size());
        for (StoreMessage item : messageList) {
            StoreMessageVo vo = new StoreMessageVo();
            vo.setId(item.getId());
            vo.setProductName(item.getProductName());
            vo.setSpecName(item.getSpecName());
            vo.setContent(item.getContent());
            vo.setCreateTime(item.getCreateTime());
            storeMessageVos.add(vo);
        }
        return Result.success(storeMessageVos);
    }


    /**
     *  查询所有消息
     * @return
     */
    @Override
    public Result<List<StoreMessageVo>> getAll() {
        Long userId = UserContext.getUserId();
        List<StoreMessage> list = lambdaQuery()
                .eq(StoreMessage::getUserId, userId)
                .orderByDesc(StoreMessage::getCreateTime)
                .list();
        if (list == null || list.isEmpty()) {
            return Result.success(Collections.emptyList());
        }
        ArrayList<StoreMessageVo> storeMessageVos = new ArrayList<>(list.size());
        for (StoreMessage item : list) {
            StoreMessageVo vo = new StoreMessageVo();
            vo.setId(item.getId());
            vo.setProductName(item.getProductName());
            vo.setSpecName(item.getSpecName());
            vo.setContent(item.getContent());
            vo.setCreateTime(item.getCreateTime());
            storeMessageVos.add(vo);
        }
        return Result.success(storeMessageVos);
    }

    private int normalizeLimit(Integer limit) {
        if (limit == null || limit <= 0) {
            return 50;
        }
        return Math.min(limit, 200);
    }
}
