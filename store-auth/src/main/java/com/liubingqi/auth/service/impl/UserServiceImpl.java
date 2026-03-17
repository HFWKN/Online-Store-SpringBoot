package com.liubingqi.auth.service.impl;

import com.liubingqi.auth.domain.po.User;
import com.liubingqi.auth.mapper.UserMapper;
import com.liubingqi.auth.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author lbq
 * @since 2026-03-17
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
