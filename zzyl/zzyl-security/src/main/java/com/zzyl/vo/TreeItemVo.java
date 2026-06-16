package com.zzyl.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@ApiModel("树形节点")
public class TreeItemVo {

    @ApiModelProperty(value = "节点ID")
    private String id;

    @ApiModelProperty(value = "节点名称")
    private String label;

    @ApiModelProperty(value = "子节点")
    private List<TreeItemVo> children;
}
