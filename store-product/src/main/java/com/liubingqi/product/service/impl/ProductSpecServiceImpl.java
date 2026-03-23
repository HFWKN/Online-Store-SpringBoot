package com.liubingqi.product.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.liubingqi.common.exception.BusinessException;
import com.liubingqi.product.domain.po.ProductSpec;
import com.liubingqi.product.domain.vo.ProductSpecVo;
import com.liubingqi.product.mapper.ProductSpecMapper;
import com.liubingqi.product.service.IProductSpecService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
        List<ProductSpecVo> voList = BeanUtil.copyToList(list, ProductSpecVo.class);

        // 如果没有数据，就返回空集合
        if(CollectionUtil.isEmpty(voList)){
            return new ArrayList<>();
        }
        return voList;
    }
}
