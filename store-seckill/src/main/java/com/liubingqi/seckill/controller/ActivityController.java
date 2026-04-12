package com.liubingqi.seckill.controller;


import com.liubingqi.common.domain.Result;
import com.liubingqi.seckill.domain.vo.ActivityVo;
import com.liubingqi.seckill.service.IActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 秒杀活动表 前端控制器
 * </p>
 *
 * @author lbq
 * @since 2026-04-07
 */
@RestController
@RequestMapping("/seckill/activity")
@RequiredArgsConstructor
@Tag(name = "秒杀活动", description = "秒杀活动相关接口")
public class ActivityController {


    private final IActivityService activityService;


    /**
     *  获取秒杀活动列表(查询全部活动 和 当前进行的活动 和 下一场)
     * @return
     */
    @GetMapping("/list")
    @Operation(summary = "获取秒杀活动列表")
    public Result<ActivityVo> selectActivity(){
        return activityService.selectActivity();
    }
}
