package com.liubingqi.auth.service.impl;

import com.liubingqi.auth.domain.po.UserLike;
import com.liubingqi.auth.mapper.UserLikeMapper;
import com.liubingqi.auth.service.IUserLikeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户收藏表 服务实现类
 * </p>
 *
 * @author lbq
 * @since 2026-03-20
 */
@Service
public class UserLikeServiceImpl extends ServiceImpl<UserLikeMapper, UserLike> implements IUserLikeService {

}
