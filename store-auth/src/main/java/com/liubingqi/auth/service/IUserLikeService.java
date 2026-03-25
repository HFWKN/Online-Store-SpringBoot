package com.liubingqi.auth.service;

import com.liubingqi.auth.domain.po.UserLike;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liubingqi.auth.domain.vo.UserLikeVo;

import java.util.List;

/**
 * <p>
 * 用户收藏表 服务类
 * </p>
 *
 * @author lbq
 * @since 2026-03-20
 */
public interface IUserLikeService extends IService<UserLike> {

    // 查询我的收藏
    List<UserLikeVo> getAll(Long userId);

}
