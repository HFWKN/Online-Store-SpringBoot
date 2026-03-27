package com.liubingqi.auth.controller;


import com.liubingqi.auth.domain.dto.UserLikeDto;
import com.liubingqi.auth.domain.po.UserLike;
import com.liubingqi.auth.domain.vo.UserLikeVo;
import com.liubingqi.auth.service.IUserLikeService;
import com.liubingqi.common.domain.Result;
import com.liubingqi.common.exception.BusinessException;
import com.liubingqi.common.utils.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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


    /**
     *  新增收藏
     * @param dto
     * @return
     */
    @PostMapping("/add")
    @Operation(summary = "新增收藏")
    public Result<Void> addLike(@RequestBody UserLikeDto dto){
        Long userId = UserContext.getUserId();
        if(userId == null){
            throw new BusinessException("无用户id");
        }

        UserLike userLike = new UserLike();
        userLike.setUserId(userId);
        userLike.setProductId(dto.getProductId());
        userLike.setCategoryId(dto.getCategoryId());
        userLike.setSpecId(dto.getSpecId());
        userLike.setCreateTime(LocalDateTime.now());
        userLike.setUpdateTime(LocalDateTime.now());
        boolean result = userLikeService.save(userLike);
        if (!result){
            return Result.fail("新增失败");
        }
        return Result.success();
    }

    @DeleteMapping("/delete")
    @Operation(summary = "取消收藏")
    public Result<Void> deleteLike(@RequestBody UserLikeDto dto){
        Long userId = UserContext.getUserId();
        if(userId == null){
            throw new BusinessException("无用户id");
        }
        boolean result = userLikeService.lambdaUpdate()
                .eq(UserLike::getUserId, userId)
                .eq(UserLike::getProductId, dto.getProductId())
                .eq(UserLike::getCategoryId, dto.getCategoryId())
                .eq(UserLike::getSpecId, dto.getSpecId())
                .remove();
        if (!result){
            return Result.fail("取消失败");
        }
        return Result.success();
    }

    @PostMapping("/getStatus")
    @Operation(summary = "查询收藏状态")
    public Result<Boolean> getStatus(@RequestBody UserLikeDto dto){
        Long userId = UserContext.getUserId();
        if(userId == null){
            throw new BusinessException("无用户id");
        }
        return Result.success(userLikeService.lambdaQuery()
                .eq(UserLike::getUserId, userId)
                .eq(UserLike::getProductId, dto.getProductId())
                .eq(UserLike::getCategoryId, dto.getCategoryId())
                .eq(UserLike::getSpecId, dto.getSpecId())
                .exists());
    }
}
