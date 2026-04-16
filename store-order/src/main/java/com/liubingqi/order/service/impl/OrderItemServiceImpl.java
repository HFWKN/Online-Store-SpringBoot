package com.liubingqi.order.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.liubingqi.common.domain.Result;
import com.liubingqi.common.feignClient.product.ProductFeignClient;
import com.liubingqi.common.feignClient.product.vo.ProductVo;
import com.liubingqi.common.utils.UserContext;
import com.liubingqi.order.domain.po.OrderItem;
import com.liubingqi.order.domain.vo.OrderVo;
import com.liubingqi.order.mapper.OrderItemMapper;
import com.liubingqi.order.service.IOrderItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单明细表 服务实现类
 * </p>
 *
 * @author lbq
 * @since 2026-03-17
 */
@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements IOrderItemService {


    private final ProductFeignClient productFeignClient;


    /**
     *  查询当前用户的所有订单
     * @param status
     * @return
     */
    @Override
    public List<OrderVo> selectAll(Integer status, String productName) {
        // 获取当前用户id
        Long userId = UserContext.getUserId();
        // 根据用户id查询他的所有订单
        List<OrderItem> list = lambdaQuery()
                .eq(OrderItem::getUserId, userId)
                .eq(status != null, OrderItem::getStatus, status)
                .like(productName != null, OrderItem::getProductName, productName)
                // 根据id倒叙
                .orderByDesc(OrderItem::getId)
                .list();
        // 如果订单列表为空，则返回空
        if(CollectionUtil.isEmpty(list)){
            return new ArrayList<>();
        }
        // 获取list中的商品id
        List<Long> productIdList = list.stream()
                .map(OrderItem::getProductId)
                .toList();
        // 远程调用。通过商品id查询商品信息
        Result<List<ProductVo>> productResult = productFeignClient.getByIds(productIdList);
        List<ProductVo> productVoList = productResult.getData();
        if (CollectionUtil.isEmpty(productVoList)){
            throw new RuntimeException("商品不存在");
        }
        // 创建map，每个商品id对应商品图片
        Map<Long,String> map = new HashMap<>();
        for (ProductVo vo : productVoList) {
            map.put(vo.getId(),vo.getMainImage());
        }
        // 拷贝订单信息到voList
        List<OrderVo> voList = BeanUtil.copyToList(list, OrderVo.class);
        // 遍历voList，设置商品图片
        for (OrderVo vo : voList) {
            vo.setMainImage(map.get(vo.getProductId()));
        }
        return voList;
    }
}
