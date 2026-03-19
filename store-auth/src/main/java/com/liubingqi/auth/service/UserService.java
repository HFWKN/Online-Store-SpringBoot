package com.liubingqi.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liubingqi.auth.domain.dto.RegisterRequest;
import com.liubingqi.auth.domain.dto.UserDto;
import com.liubingqi.auth.domain.po.User;
import com.liubingqi.auth.domain.vo.LoginResponse;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 用户登录
     *
     * @param userDto 登录请求参数
     * @return 登录响应
     */
    LoginResponse login(UserDto userDto);

    /**
     *  用户注册
     * @param registerRequest
     */
    void userRegister(RegisterRequest registerRequest);
}