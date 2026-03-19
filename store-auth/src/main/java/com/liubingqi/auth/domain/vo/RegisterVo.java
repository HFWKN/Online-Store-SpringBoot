package com.liubingqi.auth.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 注册响应VO
 */
@Data
@Schema(description = "注册响应")
public class RegisterVo {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "注册时间")
    private LocalDateTime registerTime;

    @Schema(description = "用户状态", example = "1")
    private Integer status;
}