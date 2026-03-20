package com.liubingqi.product.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liubingqi.common.constants.ResultCode;
import com.liubingqi.common.domain.PageQuery;
import com.liubingqi.common.domain.PageResult;
import com.liubingqi.common.exception.BusinessException;
import com.liubingqi.common.utils.UserContext;
import com.liubingqi.product.domain.page.PageQueryByProduct;
import com.liubingqi.product.domain.po.Product;
import com.liubingqi.product.domain.po.ProductCategory;
import com.liubingqi.product.domain.po.ProductComment;
import com.liubingqi.product.domain.vo.ProductSimpleVo;
import com.liubingqi.product.domain.vo.ProductVo;
import com.liubingqi.product.domain.vo.ProductWithCommentVo;
import com.liubingqi.product.mapper.ProductMapper;
import com.liubingqi.product.service.IProductCategoryService;
import com.liubingqi.product.service.IProductCommentService;
import com.liubingqi.product.service.IProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 商品基本信息表 服务实现类
 * </p>
 *
 * @author lbq
 * @since 2026-03-17
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements IProductService {

    private final IProductCategoryService productCategoryService;
    private final IProductCommentService productCommentService;


    /**
     *  商品列表分页查询(条件查询)
     * @param pageQuery
     * @return
     */
    @Override
    public PageResult<ProductSimpleVo> pageList(PageQueryByProduct pageQuery) {
        // 1.分页条件查询（添加空值判断，只有不为空才加条件）
        IPage<Product> pageList = lambdaQuery()
                .eq(Product::getStatus, 1) // 只能是上架的
                .eq(pageQuery.getCategoryId() != null, Product::getCategoryId, pageQuery.getCategoryId()) // 分类 ID（有值才查）
                .like(pageQuery.getProductName() != null && !pageQuery.getProductName().isEmpty(), 
                      Product::getName, pageQuery.getProductName()) // 商品名称（模糊，有值才查）
                .orderByAsc(pageQuery.getSort() != null && pageQuery.getSort() == 1, Product::getPrice) // 价格升序
                .orderByDesc(pageQuery.getSort() != null && pageQuery.getSort() == 2, Product::getPrice) // 价格降序
                .page(pageQuery.toMpPageDefaultSortByCreateTimeDesc());

        // 2.获取本页信息
        List<Product> records = pageList.getRecords();
        if (records.isEmpty()){
            return PageResult.empty(new Page<>());
        }

        // 3. 新建voList
        List<ProductSimpleVo> voList = new ArrayList<>(records.size());

        // 4. 循环遍历，封装进vo
        for (Product r : records) {
            // 4.1 新建vo
            ProductSimpleVo vo = new ProductSimpleVo();
            BeanUtil.copyProperties(r, vo);
            voList.add(vo);
        }

        // 5.返回处理结果
        return new PageResult<>(pageList.getTotal(), pageList.getPages(), voList);
    }


    /**
     *  查询商品详情页
     * @param productId
     * @return
     */
    @Override
    public ProductWithCommentVo detailed(Integer productId) {
        // 1,根据商品id查询信息
        Product product = getById(productId);
        if (product == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND, "商品不存在");
        }

        // 2,赋值
        ProductWithCommentVo vo = new ProductWithCommentVo();
        BeanUtil.copyProperties(product,vo);

        // 3,根据分类id查询分类name
        ProductCategory category = productCategoryService.getById(product.getCategoryId());
        if(category == null){
            throw new BusinessException("商品分类不存在");
        }

        // 4,赋值信息
        vo.setCompanyName(category.getName());

        // 5,查询商品评价信息
        List<ProductComment> productCommentList = productCommentService.getCommentById(productId);
        if (!productCommentList.isEmpty()){
            // 最新评论
            vo.setLatestComments(productCommentList.subList(0, Math.min(productCommentList.size(), 3)));// 获取最新3条
            // 评论总数
            vo.setCommentCount((long) productCommentList.size());
            // 全部评论
            vo.setAllComments(productCommentList);

            // 好评率
            Integer goodCommentRate = productCommentService.goodCommentRate(productId);
            if (goodCommentRate == null){
                goodCommentRate = 0;
            }
            vo.setGoodCommentRate(goodCommentRate);
        }else {
            vo.setLatestComments(new ArrayList<>());
            vo.setCommentCount(0L);
            vo.setAllComments(new ArrayList<>());
            vo.setGoodCommentRate(0);
        }

        return vo;
    }
}
