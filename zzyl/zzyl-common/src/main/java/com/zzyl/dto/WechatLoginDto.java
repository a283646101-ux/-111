package com.zzyl.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 微信登录请求DTO
 */
@Data
public class WechatLoginDto {

    @ApiModelProperty(value = "微信授权码code", required = true)
    private String code;

    @ApiModelProperty(value = "用户昵称")
    private String nickName;

    @ApiModelProperty(value = "用户头像")
    private String avatarUrl;

    @ApiModelProperty(value = "性别(0:男，1:女)")
    private Integer gender;
}
