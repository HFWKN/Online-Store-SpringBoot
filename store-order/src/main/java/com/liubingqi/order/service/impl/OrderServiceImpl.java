package com.liubingqi.order.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import com.liubingqi.common.domain.Result;
import com.liubingqi.common.exception.BusinessException;
import com.liubingqi.common.feignClient.product.ProductFeignClient;
import com.liubingqi.common.feignClient.product.vo.ProductSpecVo;
import com.liubingqi.common.feignClient.product.vo.ProductVo;
import com.liubingqi.common.feignClient.user.UserFeignClient;
import com.liubingqi.common.feignClient.user.vo.UserAddressVo;
import com.liubingqi.common.utils.UserContext;
import com.liubingqi.order.constants.OrderLuaScriptConstants;
import com.liubingqi.order.constants.OrderRedisKeyConstants;
import com.liubingqi.order.domain.dto.CreateOrderDto;
import com.liubingqi.order.domain.po.Order;
import com.liubingqi.order.domain.po.OrderItem;
import com.liubingqi.order.mapper.OrderItemMapper;
import com.liubingqi.order.mapper.OrderMapper;
import com.liubingqi.order.service.IOrderItemService;
import com.liubingqi.order.service.IOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 订单主表 服务实现类
 * </p>
 *
 * @author lbq
 * @since 2026-03-17
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {


    private final StringRedisTemplate stringRedisTemplate;
    private final OrderMapper orderMapper;
    private final ProductFeignClient productFeignClient;
    private final UserFeignClient userFeignClient;
    private final OrderItemMapper orderItemMapper;

    @Override
    public String getToken() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        String key = OrderRedisKeyConstants.ORDER_TOKEN_KEY_PREFIX + userId;
        String token = UUID.randomUUID().toString().replace("-", "");
        stringRedisTemplate.opsForValue().set(key, token, 10, TimeUnit.MINUTES);
        return token;
    }

    /**
     *  用户下单
     * @param dto
     * @param orderToken  redis中，key为order_Token
     * @return
     */
    @Override
    @Transactional
    public String userPlaceAnOrder(CreateOrderDto dto, String orderToken) {
        Long userId = UserContext.getUserId();
        String key = OrderRedisKeyConstants.ORDER_TOKEN_KEY_PREFIX + userId;
        // 先查询redis中是否有此次下单
        Long result = stringRedisTemplate.execute(OrderLuaScriptConstants.CHECK_AND_DELETE_ORDER_TOKEN_SCRIPT, Collections.singletonList(key), orderToken);
        if (result == null || result == 0L) {
            throw new BusinessException("请勿重复提交或下单已失效");
        }
        // 创建订单
        List<Order> orderList = new ArrayList<>();
        // 订单编号
        String orderNo = "order_" + System.currentTimeMillis() + "_" + new Random().nextInt(10000);
        // 获取dto中的订单商品信息list(只存商品信息，无地址等)
        List<CreateOrderDto.OrderItemDto> productInfos = dto.getItems();

        // 创建商品id list
        List<Long> productIds = new ArrayList<>();
        for (CreateOrderDto.OrderItemDto p : productInfos) {
            productIds.add(p.getProductId());
        }

        // 根据地址id查询地址信息
        Result<UserAddressVo> addressVoResult = userFeignClient.selectByAddressId(dto.getAddressId());
        UserAddressVo addressVo = addressVoResult.getData();

        // 收集dto中的规格id
        List<Long> specIds = new ArrayList<>();
        for (CreateOrderDto.OrderItemDto item : productInfos) {
            specIds.add(item.getSpecId());
        }

        // 远程调用， 根据规格id查询商品规格信息
        Result<List<ProductSpecVo>> spec = productFeignClient.getBySpecIds(specIds);
        List<ProductSpecVo> specList = spec.getData();
        if (CollectionUtil.isEmpty(specList)) {
            throw new BusinessException("无库存");
        }
        // 创建map,key为商品id value为商品规格信息
        Map<Long, ProductSpecVo> specMap = new HashMap<>();
        for (ProductSpecVo s : specList) {
            specMap.put(Long.valueOf(s.getId()), s);
        }
        // 批量查询商品规格信息，根据规格id
        List<CreateOrderDto.OrderItemDto> items = dto.getItems();
        // 创建list，存规格id
        List<Long> specIds1 = new ArrayList<>();
        for (CreateOrderDto.OrderItemDto item : items) {
            specIds1.add(item.getSpecId());
        }

        // 批量查询商品信息
        Result<List<ProductVo>> productListResult = productFeignClient.getByIds(productIds);
        List<ProductVo> productList = productListResult.getData();
        if (CollectionUtil.isEmpty(productList)) {
            throw new BusinessException("无库存");
        }
        // 创建map,key为商品id value为商品信息
        Map<Long, ProductVo> productMap = new HashMap<>();
        for (ProductVo p : productList) {
            productMap.put(p.getId(), p);
        }

        // 生成订单主表
        Order order = new Order();
        order.setOrderNo(orderNo);// 订单编号
        order.setUserId(userId); // 用户id
        order.setTotalAmount(getTotalAmount(dto.getItems())); // 订单总金额
        order.setPayAmount(dto.getPayAmount()); // 实付金额
        order.setRemark(dto.getRemark());
        order.setStatus(1); // 暂时为：已支付
        order.setPaymentTime(LocalDateTime.now());// 支付时间
        // 发货时间为当前时间+1天
        order.setDeliveryTime(LocalDateTime.now().plusDays(1));
        order.setReceiverName(addressVo.getReceiverName());// 收货人
        order.setReceiverPhone(addressVo.getReceiverPhone());// 收货电话
        order.setReceiverAddress(addressVo.getAddress());// 收货地址
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        // 插入订单主表
        int insert = orderMapper.insert(order);
        if (insert != 1){
            throw new BusinessException("订单插入失败");
        }

        // 遍历商品信息list
        for (CreateOrderDto.OrderItemDto p : productInfos) {
            // 在同一个事务里，先原子扣库存，全部成功后再落订单
            Integer num = p.getNum();
            Long productId = p.getProductId();
            Long specId = p.getSpecId();
            // 校验库存 --- 远程调用
            Result<Integer> resultInfo = productFeignClient.userPlaceAnOrder(num, productId, specId);
            Integer update = resultInfo.getData();
            if (update == null) {
                throw new BusinessException("库存校验出错");
            }
            // 更改0条，表示无库存或更新失败
            if (update != 1) {
                throw new BusinessException(productId + specId + "无库存或更新失败");
            }
            // 创建订单明细对象
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId()); // 订单id
            orderItem.setOrderNo(orderNo); // 订单编号
            orderItem.setProductId(productId); // 商品id
            orderItem.setProductName(productMap.get(productId).getName()); // 商品名称快照
            orderItem.setSpecId(p.getSpecId()); // 商品规格id
            orderItem.setProductSpec(specMap.get(specId).getProductSpec()); // 商品规格
            orderItem.setColor(specMap.get(specId).getColor()); // 颜色
            orderItem.setProductPrice(specMap.get(specId).getSpecPrice()); // 购买单价快照
            orderItem.setQuantity(p.getNum()); // 数量
            orderItem.setTotalPrice(specMap.get(specId).getSpecPrice().multiply(new BigDecimal(p.getNum()))); // 小计金额

            // 插入订单明细表
            int insert1 = orderItemMapper.insert(orderItem);
            if (insert1 != 1) {
                throw new BusinessException("订单明细插入失败");
            }
        }
        return "下单成功";
    }


    // 查询商品总价
    private BigDecimal getTotalAmount(List<CreateOrderDto.OrderItemDto> productInfos) {
        if (CollectionUtil.isEmpty(productInfos)) {
            throw new BusinessException("订单商品不能为空");
        }
        List<Long> specIds = new ArrayList<>(productInfos.size());
        for (CreateOrderDto.OrderItemDto item : productInfos) {
            if (item.getSpecId() == null || item.getProductId() == null || item.getNum() == null || item.getNum() <= 0) {
                throw new BusinessException("订单商品参数不合法");
            }
            specIds.add(item.getSpecId());
        }
        Result<List<ProductSpecVo>> result = productFeignClient.getBySpecIds(specIds);
        List<ProductSpecVo> specList = result == null ? null : result.getData();
        if (CollectionUtil.isEmpty(specList)) {
            throw new BusinessException("商品规格价格不存在");
        }
        Map<Long, ProductSpecVo> specMap = new HashMap<>(specList.size());
        for (ProductSpecVo specVo : specList) {
            if (specVo.getId() == null || specVo.getProductId() == null || specVo.getSpecPrice() == null) {
                continue;
            }
            specMap.put(specVo.getId().longValue(), specVo);
        }
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CreateOrderDto.OrderItemDto item : productInfos) {
            ProductSpecVo specVo = specMap.get(item.getSpecId());
            if (specVo == null || specVo.getSpecPrice() == null) {
                throw new BusinessException("商品规格价格不存在");
            }
            if (!Objects.equals(specVo.getProductId().longValue(), item.getProductId())) {
                throw new BusinessException("商品与规格不匹配");
            }
            totalAmount = totalAmount.add(specVo.getSpecPrice().multiply(BigDecimal.valueOf(item.getNum())));
        }
        return totalAmount;
    }
}
