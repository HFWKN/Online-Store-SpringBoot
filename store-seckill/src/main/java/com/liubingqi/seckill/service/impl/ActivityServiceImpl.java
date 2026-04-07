package com.liubingqi.seckill.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liubingqi.common.domain.Result;
import com.liubingqi.seckill.domain.po.Activity;
import com.liubingqi.seckill.domain.vo.ActivityVo;
import com.liubingqi.seckill.mapper.ActivityMapper;
import com.liubingqi.seckill.service.IActivityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 秒杀活动表 服务实现类
 * </p>
 *
 * @author lbq
 * @since 2026-04-07
 */
@Service
@RequiredArgsConstructor
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements IActivityService {

    private final ActivityMapper activityMapper;

    /**
     *  获取秒杀活动列表(查询全部活动 和 当前进行的活动 和 下一场)
     * @return
     */
    @Override
    public Result<ActivityVo> selectActivity() {
        // 1.先查询全部活动
        ActivityVo activityVo = new ActivityVo();
        List<Activity> activityList = activityMapper.selectList(new QueryWrapper<>());
        // 2.活动列表为空时返回空列表,不为空则返回查询结果
        activityVo.setActivityList(CollectionUtil.isEmpty(activityList) ? new ArrayList<>() : activityList);

        // 4, 查询当前正在进行的秒杀活动
        // 4.1 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        // 当前时间在活动时间段内
        Activity nowActivity = lambdaQuery()
                .le(Activity::getBeginTime, now)
                .ge(Activity::getEndTime, now)
                .one();
        // set 当前进行中的活动(为空时返回空对象)
        activityVo.setCurrentActivity(nowActivity == null ? new Activity() : nowActivity);

        // 5.查询下一场秒杀活动
        Activity nextActivity = lambdaQuery()
                .ge(Activity::getBeginTime, now)
                .orderByAsc(Activity::getBeginTime)
                .last("limit 1")
                .one();
        // set 下一场秒杀活动(为空时返回空对象)
        activityVo.setNextActivity(nextActivity == null ? new Activity() : nextActivity);

        return Result.success(activityVo);
    }
}
