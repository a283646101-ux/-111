package com.zzyl.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@ApiModel("树形结构")
public class TreeVo {

    @ApiModelProperty(value = "树形节点")
    private List<TreeItemVo> items;
}
