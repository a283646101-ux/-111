package com.zzyl.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel("菜单VO")
public class MenuVo implements Serializable {

    @ApiModelProperty(value = "路由名字")
    private String name;

    @ApiModelProperty(value = "资源编号")
    private String resourceNo;

    @ApiModelProperty(value = "父资源编号")
    private String parentResourceNo;

    @ApiModelProperty(value = "请求路径")
    private String path;

    @ApiModelProperty(value = "层级名称展示")
    private String redirect;

    @ApiModelProperty(value = "子菜单")
    private List<MenuVo> children = new ArrayList<>();

    @ApiModelProperty(value = "meta属性")
    private MenuMetaVo meta;
}
