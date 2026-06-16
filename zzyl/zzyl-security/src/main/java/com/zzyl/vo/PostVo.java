package com.zzyl.vo;

import com.zzyl.base.BaseVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@ApiModel("岗位VO")
public class PostVo extends BaseVo {

    @ApiModelProperty(value = "部门编号")
    private String deptNo;

    @ApiModelProperty(value = "岗位编号")
    private String postNo;

    @ApiModelProperty(value = "岗位名称")
    private String postName;

    @ApiModelProperty(value = "排序")
    private Integer sortNo;

    @ApiModelProperty(value = "职位对应部门")
    private DeptVo deptVo;

    @ApiModelProperty(value = "是否启用（0:启用，1:禁用）")
    private String dataState;
}
