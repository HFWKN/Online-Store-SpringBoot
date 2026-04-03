package com.liubingqi.product.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.liubingqi.common.exception.BusinessException;
import com.liubingqi.product.domain.po.Product;
import com.liubingqi.product.domain.po.ProductSpec;
import com.liubingqi.product.domain.vo.ProductSpecVo;
import com.liubingqi.product.mapper.ProductSpecMapper;
import com.liubingqi.product.service.IProductSpecService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 商品规格表 服务实现类
 * </p>
 *
 * @author lbq
 * @since 2026-03-23
 */
@Service
public class ProductSpecServiceImpl extends ServiceImpl<ProductSpecMapper, ProductSpec> implements IProductSpecService {


    /**
     *  查询该商品的全部规格
     * @param productId
     * @return
     */
    @Override
    public List<ProductSpecVo> selectAll(Long productId) {
        if(productId == null){
            throw new BusinessException("没有传入商品id");
        }
        // 根据商品id查询所属的商品规格
        List<ProductSpec> list = lambdaQuery()
                .eq(ProductSpec::getProductId, productId)
                .list();
        // 转VO
        List<ProductSpecVo> voList = list.stream().map(po -> {
            ProductSpecVo vo = BeanUtil.copyProperties(po, ProductSpecVo.class);
            vo.setProductSpec(po.getProductSpec());
            return vo;
        }).collect(Collectors.toList());

        // 如果没有数据，就返回空集合
        if(CollectionUtil.isEmpty(voList)){
            return new ArrayList<>();
        }
        return voList;
    }


    /**
     *  批量查询商品规格
     * @param productIds
     * @return
     */
    @Override
    public List<Map<Long, ProductSpecVo>> selectByIds(List<Long> productIds) {
        List<ProductSpec> list = lambdaQuery()
                .in(ProductSpec::getProductId, productIds)
                .list();
        if (CollectionUtil.isEmpty(list)){
            return new ArrayList<>();
        }
        // 转VO
        List<Map<Long, ProductSpecVo>> mapList = new ArrayList<>();
        // 循环
        for (ProductSpec productSpec : list) {
            Map<Long, ProductSpecVo> map = new HashMap<>();
            ProductSpecVo vo = BeanUtil.copyProperties(productSpec, ProductSpecVo.class);
            vo.setProductSpec(productSpec.getProductSpec());
            map.put(Long.valueOf(vo.getId()),vo);
            mapList.add(map);
        }

        return mapList;
    }


   /* *//**
     *  远程调用，回滚库存
     * @param num
     * @param productId
     * @param specId
     * @return
     *//*
    @Override
    @Transactional
    public Integer rollbackStock(Integer num, Long productId, Long specId) {
        // 根据商品id和规格id去恢复库存
        ProductSpec productSpec = lambdaQuery()
                .eq(ProductSpec::getProductId, productId)
                .eq(ProductSpec::getId, specId)
                .one();
        if(productSpec == null){
            throw new BusinessException("商品信息为空");
        }
        // 恢复库存
        productSpec.setStock(productSpec.getStock() + num);
        // 售卖数量=售卖数量-num
        productSpec.setSaleNum(productSpec.getSaleNum() - num);
        if (!updateById(productSpec)){
            throw new BusinessException("回滚库存失败");
        }
        return 1;
    }*/

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer userPlaceAnOrder(Integer num, Long productId, Long specId) {
        if (num == null || num <= 0 || productId == null || specId == null) {
            throw new BusinessException("扣库存参数不合法");
        }
        return baseMapper.userPlaceAnOrder(num, productId, specId);
    }
}
