package com.liubingqi.product.service.impl;

import com.liubingqi.product.domain.po.Product;
import com.liubingqi.product.mapper.ProductMapper;
import com.liubingqi.product.service.IProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品基本信息表 服务实现类
 * </p>
 *
 * @author lbq
 * @since 2026-03-17
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements IProductService {

}
