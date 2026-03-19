package com.liubingqi.auth.domain.po;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 公司信息表
 * </p>
 *
 * @author lbq
 * @since 2026-03-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("store_company")
@Schema(name = "Company对象", description = "公司信息表")
public class Company implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "公司ID(主键)")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "公司名称")
    private String name;

    @Schema(description = "状态 (1:正常, 0:禁用)")
    private Integer status;

    @Schema(description = "注册资金 (单位: 万)")
    private BigDecimal capital;

    @Schema(description = "法定代表人")
    private String representative;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "修改时间")
    private LocalDateTime updateTime;

}
