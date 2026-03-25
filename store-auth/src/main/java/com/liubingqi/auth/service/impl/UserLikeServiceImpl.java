package com.liubingqi.auth.service.impl;

import com.liubingqi.auth.domain.po.UserLike;
import com.liubingqi.auth.domain.vo.UserLikeVo;
import com.liubingqi.auth.mapper.UserLikeMapper;
import com.liubingqi.auth.service.IUserLikeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liubingqi.common.domain.Result;
import com.liubingqi.common.feignClient.product.ProductFeignClient;
import com.liubingqi.common.feignClient.product.vo.ProductSpecVo;
import com.liubingqi.common.feignClient.product.vo.ProductVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户收藏表 服务实现类
 * </p>
 *
 * @author lbq
 * @since 2026-03-20
 */
@Service
@RequiredArgsConstructor
public class UserLikeServiceImpl extends ServiceImpl<UserLikeMapper, UserLike> implements IUserLikeService {


    private final ProductFeignClient productFeignClient;

    /**
     *  查询我的收藏
     * @param userId
     * @return
     */
    @Override
    public List<UserLikeVo> getAll(Long userId) {
        // 查询我的收藏
        List<UserLike> list = lambdaQuery()
                .eq(UserLike::getUserId, userId)
                .list();

        //获取商品id
        List<Long> productIdList = new ArrayList<>(list.size());
        for (UserLike u : list) {
            productIdList.add(u.getProductId());
        }
        // 如果没有收藏，直接返回空集合
        if (productIdList.isEmpty()) {
            return new ArrayList<>();
        }
  /*      //获取分类id
        List<Long> categoryIdList = new ArrayList<>(list.size());
        for (UserLike u : list) {
            categoryIdList.add(u.getCategoryId());
        }*/
        // 远程调用，根据商品id获取商品信息和分类信息
        Result<List<ProductVo>> productResult = productFeignClient.getByIds(productIdList);
        List<ProductVo> productVoList = productResult.getData();

        // 创建map，每个商品id对应商品信息
        Map<Long,ProductVo> map = new HashMap<>();
        if (productVoList != null) {
            for (ProductVo vo : productVoList) {
                map.put(vo.getId(),vo);
            }
        }
        // 根据商品id查询对应的规格信息
        Result<List<Map<Long, ProductSpecVo>>> productSpecList = productFeignClient.selectByIds(productIdList);
        // 获取规格信息
        List<Map<Long, ProductSpecVo>> specList = productSpecList.getData();
        // 创建map，每个规格id对应规格信息
        Map<Long,ProductSpecVo> specMap = new HashMap<>();
        // 循环遍历规格list信息
        if (specList != null) {
            for (Map<Long, ProductSpecVo> m : specList) {
                specMap.putAll(m);
            }
        }
        // 创建VO
        List<UserLikeVo> voList = new ArrayList<>();
        // 遍历我的收藏
        for (UserLike u : list) {
            UserLikeVo ulv = new UserLikeVo();
            ulv.setId(u.getId());
            ulv.setUserId(userId);
            ulv.setProductId(u.getProductId());
            
            // 设置商品基本信息
            ProductVo pVo = map.get(u.getProductId());
            if (pVo != null) {
                ulv.setProductName(pVo.getName());
                ulv.setMainImage(pVo.getMainImage());
                ulv.setCategoryId(pVo.getCategoryId());
                ulv.setCategoryName(pVo.getCategoryName());
                ulv.setPrice(pVo.getPrice());
            }

            // 设置规格信息
            if (u.getSpecId() != null) {
                ProductSpecVo specVo = specMap.get(u.getSpecId());
                if (specVo != null) {
                    ulv.setProductSpec(specVo.getColor() + " " + specVo.getSpec());
                }
            }

            voList.add(ulv);
        }
        return voList;
    }
}
