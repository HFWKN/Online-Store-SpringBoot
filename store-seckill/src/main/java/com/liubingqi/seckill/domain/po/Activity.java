package com.liubingqi.seckill.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 秒杀活动表
 * </p>
 *
 * @author lbq
 * @since 2026-04-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("store_activity")
public class Activity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 秒杀活动ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 秒杀活动名字（场次）
     */
    private String name;

    /**
     *  开始时间
     */
    private LocalDateTime beginTime;

    /**
     *  结束时间
     */
    private LocalDateTime endTime;

}
