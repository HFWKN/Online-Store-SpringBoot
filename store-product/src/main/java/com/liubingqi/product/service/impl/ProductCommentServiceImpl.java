package com.liubingqi.product.service.impl;

import com.liubingqi.product.domain.po.ProductComment;
import com.liubingqi.product.mapper.ProductCommentMapper;
import com.liubingqi.product.service.IProductCommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品评价表 服务实现类
 * </p>
 *
 * @author lbq
 * @since 2026-03-20
 */
@Service
public class ProductCommentServiceImpl extends ServiceImpl<ProductCommentMapper, ProductComment> implements IProductCommentService {

}
