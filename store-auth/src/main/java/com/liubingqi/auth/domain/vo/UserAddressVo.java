package com.liubingqi.auth.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 *  收获地址VO
 */
@Data
public class UserAddressVo {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userid;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 逻辑删除: 0-未删除, 1-已删除
     */
    private Integer isDelete;

    /**
     * 是否默认地址: 0-否, 1-是
     */
    private Integer isDefault;
}
