package com.liubingqi.auth.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 *  用户地址dto
 */
@Data
public class UserAddressDto {

    /**
     *  主键id
     */
    private Long id;
    /**
     * 用户ID
     */
    private Long userid;

    /**
     *  收货人姓名
     */
    private String receiverName;

    /**
     * 收货人电话
     */
    private String receiverPhone;

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
