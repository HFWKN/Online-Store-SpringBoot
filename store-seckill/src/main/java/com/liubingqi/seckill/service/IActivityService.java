package com.liubingqi.seckill.service;

import com.liubingqi.common.domain.Result;
import com.liubingqi.seckill.domain.po.Activity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liubingqi.seckill.domain.vo.ActivityVo;

/**
 * <p>
 * 秒杀活动表 服务类
 * </p>
 *
 * @author lbq
 * @since 2026-04-07
 */
public interface IActivityService extends IService<Activity> {

    // 获取秒杀活动列表(查询全部活动 和 当前进行的活动 和 下一场)
    Result<ActivityVo> selectActivity();

}
