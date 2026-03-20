package com.liubingqi.product.service;

import com.liubingqi.common.domain.PageQuery;
import com.liubingqi.common.domain.PageResult;
import com.liubingqi.product.domain.page.PageQueryByProduct;
import com.liubingqi.product.domain.po.Product;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liubingqi.product.domain.vo.ProductSimpleVo;
import com.liubingqi.product.domain.vo.ProductVo;
import com.liubingqi.product.domain.vo.ProductWithCommentVo;

/**
 * <p>
 * 商品基本信息表 服务类
 * </p>
 *
 * @author lbq
 * @since 2026-03-17
 */
public interface IProductService extends IService<Product> {


    //商品列表分页查询
    PageResult<ProductSimpleVo> pageList(PageQueryByProduct pageQuery);

    //查询商品详情页
    ProductWithCommentVo detailed(Integer productId);
}
