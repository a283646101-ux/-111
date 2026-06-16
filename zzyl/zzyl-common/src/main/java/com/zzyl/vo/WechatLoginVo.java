package com.zzyl.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 微信登录响应VO
 */
@Data
public class WechatLoginVo {

    @ApiModelProperty(value = "客户ID")
    private Long id;

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

    @ApiModelProperty(value = "JWT令牌")
    private String token;

    @ApiModelProperty(value = "是否新用户(true:新用户，需要绑定手机号)")
    private Boolean isNew;
}
