package com.liubingqi.seckill.domain.vo;

import lombok.Data;

/**
 * 给前端返回状态码的VO
 */
@Data
public class CodeInfoVo {

    // 状态码
    private Integer code;
    // 状态码对应的消息
    private String message;
}
