package com.zzyl.vo;

import com.zzyl.base.BaseVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@ApiModel("部门VO")
public class DeptVo extends BaseVo {

    @ApiModelProperty(value = "父部门编号")
    private String parentDeptNo;

    @ApiModelProperty(value = "部门编号")
    private String deptNo;

    @ApiModelProperty(value = "部门名称")
    private String deptName;

    @ApiModelProperty(value = "排序")
    private Integer sortNo;

    @ApiModelProperty(value = "负责人Id")
    private Long leaderId;

    @ApiModelProperty(value = "负责人姓名")
    private String leaderName;

    @ApiModelProperty(value = "子部门数量")
    private Integer childCount;
}
