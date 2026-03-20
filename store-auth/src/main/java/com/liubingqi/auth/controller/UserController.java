package com.liubingqi.auth.controller;

import com.liubingqi.auth.domain.dto.RegisterRequest;
import com.liubingqi.auth.domain.dto.UserDto;
import com.liubingqi.auth.domain.po.User;
import com.liubingqi.auth.domain.vo.LoginResponse;
import com.liubingqi.auth.service.UserService;
import com.liubingqi.common.constants.ResultCode;
import com.liubingqi.common.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/user")
@Tag(name = "用户认证", description = "用户登录、注册、认证相关接口")
@Validated
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    /**
     *  用户登录接口
     * @param userDto
     * @return 登录结果
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<LoginResponse> userLogin(
            @RequestBody @Valid 
            @Parameter(description = "登录请求参数", required = true) 
            UserDto userDto) {
        
        try {
            LoginResponse loginResponse = userService.login(userDto);
            return Result.success("登录成功", loginResponse);
        } catch (RuntimeException e) {
            log.warn("用户登录失败: {}", e.getMessage());
            return Result.error(ResultCode.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            log.error("用户登录异常: {}", e.getMessage(), e);
            return Result.error("登录失败，系统异常");
        }
    }


    /**
     *  用户注册
     * @param registerRequest
     * @return
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "用户名密码注册")
    public Result<String> userRegister(
            @RequestBody @Valid 
            @Parameter(description = "注册请求参数", required = true) 
            RegisterRequest registerRequest) {
        try {
            userService.userRegister(registerRequest);
            return Result.success("注册成功");
        } catch (RuntimeException e) {
            log.warn("用户注册失败: {}", e.getMessage());
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("用户注册异常: {}", e.getMessage(), e);
            return Result.error("注册失败，系统异常");
        }
    }
}
