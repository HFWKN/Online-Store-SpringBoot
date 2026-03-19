package com.liubingqi.product.service.impl;

import com.liubingqi.product.domain.po.ProductCategory;
import com.liubingqi.product.mapper.ProductCategoryMapper;
import com.liubingqi.product.service.IProductCategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品分类表 服务实现类
 * </p>
 *
 * @author lbq
 * @since 2026-03-19
 */
@Service
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements IProductCategoryService {

}
