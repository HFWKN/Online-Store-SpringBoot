package com.liubingqi.auth.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liubingqi.auth.constants.JwtConstants;
import com.liubingqi.auth.domain.dto.RegisterRequest;
import com.liubingqi.auth.domain.dto.UserDto;
import com.liubingqi.auth.domain.po.User;
import com.liubingqi.auth.domain.vo.LoginResponse;
import com.liubingqi.auth.mapper.UserMapper;
import com.liubingqi.auth.service.UserService;
import com.liubingqi.auth.utils.JwtTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final JwtTool jwtTool;

    /**
     *  用户登录接口
     * @param userDto 登录请求参数
     * @return
     */
    @Override
    public LoginResponse login(UserDto userDto) {
        // 1. 参数校验
        if (StrUtil.isBlank(userDto.getUsername()) || StrUtil.isBlank(userDto.getPassword())) {
            throw new RuntimeException("用户名和密码不能为空");
        }

        // 2. 查询用户 - 使用MyBatis-Plus便捷方法
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, userDto.getUsername());
        User user = getOne(queryWrapper);

        if (user == null) {
            log.warn("用户登录失败，用户不存在: {}", userDto.getUsername());
            throw new RuntimeException("用户名或密码错误");
        }

        // 3. 验证用户状态
        if (user.getStatus() == null || user.getStatus() != 1) {
            log.warn("用户登录失败，账号已被禁用: {}", userDto.getUsername());
            throw new RuntimeException("账号已被禁用");
        }

        // 4. 验证密码
        if (!userDto.getPassword().equals(user.getPassword())) {
            log.warn("用户登录失败，密码错误: {}", userDto.getUsername());
            throw new RuntimeException("用户名或密码错误");
        }

        // 5. 生成JWT令牌
        String token = jwtTool.generateToken(user.getId(), user.getUsername());

        // 6. 构建响应数据
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setAccessToken(token);
        loginResponse.setTokenType("Bearer");
        loginResponse.setExpiresIn(JwtConstants.EXPIRE);

        // 构建用户信息
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setNickname(user.getNickname());
        userInfo.setEmail(user.getEmail());
        userInfo.setPhone(user.getPhone());
        userInfo.setStatus(user.getStatus());
        
        loginResponse.setUserInfo(userInfo);

        log.info("用户登录成功: {}", userDto.getUsername());
        return loginResponse;
    }

    /**
     *  用户注册
     * @param registerRequest
     */
    @Override
    public void userRegister(RegisterRequest registerRequest) {
        // 1. 确认密码校验
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new RuntimeException("两次密码输入不一致");
        }

        // 2. 检查用户名是否已存在 
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, registerRequest.getUsername());
        User existUser = getOne(queryWrapper);
        
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 3. 创建用户对象
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(registerRequest.getPassword());
        user.setNickname(registerRequest.getUsername()); // 默认昵称为用户名
        user.setEmail(registerRequest.getEmail()); // 设置邮箱
        user.setStatus(1); // 正常状态
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // 4. 保存用户
        boolean result = save(user);
        if (!result) {
            throw new RuntimeException("注册失败，请重试");
        }

        log.info("用户注册成功: {}", registerRequest.getUsername());
    }
}