package com.zzyl.dto;

import com.zzyl.base.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("资源菜单DTO")
public class ResourceDto extends BaseDto {

    @ApiModelProperty(value = "资源编号")
    private String resourceNo;

    @ApiModelProperty(value = "父资源编号")
    private String parentResourceNo;

    @ApiModelProperty(value = "资源名称")
    private String resourceName;

    @ApiModelProperty(value = "资源类型：s平台 c目录 m菜单 r按钮")
    private String resourceType;

    @ApiModelProperty(value = "请求地址")
    private String requestPath;

    @ApiModelProperty(value = "权限标识")
    private String label;

    @ApiModelProperty(value = "排序")
    private Integer sortNo;

    @ApiModelProperty(value = "图标")
    private String icon;

    @ApiModelProperty(value = "是否启用（0:启用，1:禁用）")
    private String dataState;

    @ApiModelProperty(value = "层级")
    private Integer level;

    @ApiModelProperty(value = "批量资源编号")
    private String resourceNos;
}
