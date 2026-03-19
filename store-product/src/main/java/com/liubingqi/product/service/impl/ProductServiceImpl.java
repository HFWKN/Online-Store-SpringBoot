package com.liubingqi.product.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liubingqi.common.domain.PageQuery;
import com.liubingqi.common.domain.PageResult;
import com.liubingqi.product.domain.page.PageQueryByProduct;
import com.liubingqi.product.domain.po.Product;
import com.liubingqi.product.domain.vo.ProductSimpleVo;
import com.liubingqi.product.domain.vo.ProductVo;
import com.liubingqi.product.mapper.ProductMapper;
import com.liubingqi.product.service.IProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements IProductService {


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
                .page(pageQuery.toMpPageDefaultSortByCreateTimeDesc());

        // 2.获取本页信息
        List<Product> records = pageList.getRecords();

        // 3. 新建voList
        List<ProductSimpleVo> voList = new ArrayList<>(records.size());

        // 4. 循环遍历，封装进vo
        for (Product r : records) {
            // 4.1 新建vo
            ProductSimpleVo vo = new ProductSimpleVo();
            BeanUtil.copyProperties(r, vo);
            // 4.2 设置状态描述
            vo.setStatusDesc(r.getStatus() == 1 ? "上架" : "下架");
            voList.add(vo);
        }

        // 5.返回处理结果
        return new PageResult<>(pageList.getTotal(), pageList.getPages(), voList);
    }
}
