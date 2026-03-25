package com.liubingqi.auth.controller;


import com.liubingqi.auth.domain.vo.UserLikeVo;
import com.liubingqi.auth.service.IUserLikeService;
import com.liubingqi.common.domain.Result;
import com.liubingqi.common.exception.BusinessException;
import com.liubingqi.common.utils.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 用户收藏表 前端控制器
 * </p>
 *
 * @author lbq
 * @since 2026-03-20
 */
@RestController
@RequestMapping("/user/like")
@RequiredArgsConstructor
@Tag(name = "我的收藏", description = "")
public class UserLikeController {


    private final IUserLikeService userLikeService;


    /**
     *  查询我的收藏
     */
    @GetMapping("/getAll")
    @Operation(summary = "查询我的收藏")
    public Result<List<UserLikeVo>> getAll(){
        Long userId = UserContext.getUserId();
        if(userId == null){
            throw new BusinessException("无用户id");
        }
        List<UserLikeVo> voList = userLikeService.getAll(userId);
        return Result.success(voList);
    }
}
