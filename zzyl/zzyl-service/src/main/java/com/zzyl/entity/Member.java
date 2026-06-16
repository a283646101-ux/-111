package com.zzyl.entity;

import com.zzyl.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 客户实体类（微信用户）
 */
@Data
public class Member extends BaseEntity {

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "微信OpenID")
    private String openId;

    @ApiModelProperty(value = "性别(0:男，1:女)")
    private Integer gender;
}
