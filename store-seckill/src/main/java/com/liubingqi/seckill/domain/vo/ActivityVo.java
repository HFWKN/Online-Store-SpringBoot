package com.liubingqi.seckill.domain.vo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.liubingqi.seckill.domain.po.Activity;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 秒杀活动列表VO
 */
@Data
public class ActivityVo {

    /**
     *  秒杀活动列表
     */
    private List<Activity> activityList;

    /**
     *  当前进行的秒杀活动
     */
    private Activity currentActivity;

    /**
     *  下一场秒杀活动
     */
    private Activity nextActivity;
}
