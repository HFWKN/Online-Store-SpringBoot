package com.liubingqi.seckill.utils;

import com.liubingqi.seckill.constants.Result_Code;
import com.liubingqi.seckill.domain.vo.CodeInfoVo;

/**
 * CodeInfoVo 构建工具
 */
public final class CodeInfoUtils {

    private CodeInfoUtils() {
    }

    public static CodeInfoVo of(Result_Code resultCode) {
        CodeInfoVo vo = new CodeInfoVo();
        vo.setCode(resultCode.getCode());
        vo.setMessage(resultCode.getMessage());
        return vo;
    }
}
