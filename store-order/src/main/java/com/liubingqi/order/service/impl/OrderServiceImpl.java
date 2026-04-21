package com.liubingqi.order.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import com.liubingqi.common.domain.Result;
import com.liubingqi.common.domain.mq.SeckillOrderMessage;
import com.liubingqi.common.exception.BusinessException;
import com.liubingqi.common.feignClient.product.ProductFeignClient;
import com.liubingqi.common.feignClient.product.vo.ProductSpecVo;
import com.liubingqi.common.feignClient.product.vo.ProductVo;
import com.liubingqi.common.feignClient.seckill.SeckillFeignClient;
import com.liubingqi.common.feignClient.seckill.dto.StockDto;
import com.liubingqi.common.feignClient.user.UserFeignClient;
import com.liubingqi.common.feignClient.user.vo.UserAddressVo;
import com.liubingqi.common.utils.UserContext;
import com.liubingqi.order.constants.OrderLuaScriptConstants;
import com.liubingqi.order.constants.OrderRedisKeyConstants;
import com.liubingqi.order.domain.dto.CreateOrderDto;
import com.liubingqi.order.domain.po.Order;
import com.liubingqi.order.domain.po.OrderItem;
import com.liubingqi.order.domain.po.StoreMessage;
import com.liubingqi.order.domain.vo.OrderVo;
import com.liubingqi.order.mapper.OrderItemMapper;
import com.liubingqi.order.mapper.OrderMapper;
import com.liubingqi.order.service.IMessageService;
import com.liubingqi.order.service.IOrderItemService;
import com.liubingqi.order.service.IOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

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
    private final SeckillFeignClient seckillFeignClient;
    private final IMessageService messageService;


    /**
     *  获取下单token
     * @return
     */
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
     * @param orderToken
     * @return
     */
    @Override
    @GlobalTransactional
    public String userPlaceAnOrder(CreateOrderDto dto, String orderToken) {
        Long userId = UserContext.getUserId();
        String key = OrderRedisKeyConstants.ORDER_TOKEN_KEY_PREFIX + userId;
        // 先查询redis中是否有此次下单
        Long result = stringRedisTemplate.execute(OrderLuaScriptConstants.CHECK_AND_DELETE_ORDER_TOKEN_SCRIPT, Collections.singletonList(key), orderToken);
        if (result == 0L) {
            throw new BusinessException("请勿重复提交或下单已失效");
        }
        // 调用下单方法，生成订单
        return createOrderCore(dto, userId);
    }


    /**
     *  下单 - MQ
     * @param order
     * @return
     */
    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
    public String createOrderFromMq(SeckillOrderMessage order) {
        if (order == null) {
            throw new BusinessException("消息为空");
        }
        if (order.getUserId() == null) {
            throw new BusinessException("消息缺少用户信息");
        }
        if (order.getAddressId() == null) {
            throw new BusinessException("消息缺少地址信息");
        }
        if (CollectionUtil.isEmpty(order.getItems())) {
            throw new BusinessException("消息缺少下单明细");
        }

        // 创建下单DTO
        CreateOrderDto dto = new CreateOrderDto();
        dto.setAddressId(order.getAddressId());
        dto.setPayAmount(order.getPayAmount());
        dto.setRemark(order.getRemark());

        // 创建秒杀库存DTO
        StockDto stockDto = new StockDto();

        // 创建商品信息集合
        List<CreateOrderDto.OrderItemDto> itemDtoList = new ArrayList<>(order.getItems().size());
        for (SeckillOrderMessage.OrderItem sourceItem : order.getItems()) {
            if (sourceItem == null || sourceItem.getProductId() == null || sourceItem.getSpecId() == null || sourceItem.getNum() == null) {
                throw new BusinessException("消息明细参数不完整");
            }
            CreateOrderDto.OrderItemDto itemDto = new CreateOrderDto.OrderItemDto();
            itemDto.setProductId(sourceItem.getProductId());
            itemDto.setSpecId(sourceItem.getSpecId());
            itemDto.setNum(sourceItem.getNum());
            itemDtoList.add(itemDto);

            // 往秒杀库存DTO中set数据
            stockDto.setProductId(sourceItem.getProductId());
            stockDto.setActivityId(order.getActivityId());
            stockDto.setSpecId(sourceItem.getSpecId());
        }
        dto.setItems(itemDtoList);

        // 调用生成订单方法
        try {
            // 调用生成订单方法
/*            if(order != null){
                // 测试用：强制失败，观察是否进入死信并写通知
                throw new BusinessException("测试异常：扣库存后强制失败");
            }*/
            createOrderCore(dto, order.getUserId());
            // 远程调用，调用扣减秒杀库存的方法
            seckillFeignClient.deductStock(stockDto);
        }catch (Exception e){
            throw new BusinessException("下单失败{生成订单or扣减库存}",e);
        }
        return "";
    }

    /**
     *  生成订单信息 - 通用
     * @param dto
     * @param userId
     * @return
     */
    private String createOrderCore(CreateOrderDto dto, Long userId) {
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
        Result<UserAddressVo> addressVoResult = userFeignClient.selectByAddressIdAndUserId(dto.getAddressId(), userId);
        UserAddressVo addressVo = addressVoResult.getData();
        if (addressVo == null) {
            throw new BusinessException("收货地址不存在或无权限访问");
        }

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
                throw new BusinessException("商品ID"+productId + "/ 规格ID" +specId + " 无库存或更新失败");
            }
            // 生成订单明细编号
            String orderItemNo = "order_item_" + System.currentTimeMillis() + "_" + new Random().nextInt(10000);
            // 创建订单明细对象
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId()); // 订单id
            orderItem.setUserId(userId);// 用户id
            orderItem.setStatus(1);// 订单状态: 已支付
            orderItem.setOrderNo(orderNo); // 订单编号
            orderItem.setOrderItemNo(orderItemNo);
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
        // 下单成功，往信息表插入信息
        StoreMessage storeMessage = new StoreMessage();
        storeMessage.setContent("下单成功,请前往“我的订单”界面查看详细");
        storeMessage.setUserId(userId);
        storeMessage.setProductName("");
        storeMessage.setSpecName("");
        storeMessage.setCreateTime(LocalDateTime.now());
        storeMessage.setUpdateTime(LocalDateTime.now());
        messageService.save(storeMessage);
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
