package com.zzyl.vo;

import com.zzyl.base.BaseVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AlertRuleVo extends BaseVo {

    @ApiModelProperty(value = "所属产品的key")
    private String productKey;

    @ApiModelProperty(value = "产品名称")
    private String productName;

    @ApiModelProperty(value = "模块的key")
    private String moduleId;

    @ApiModelProperty(value = "模块名称")
    private String moduleName;

    @ApiModelProperty(value = "功能名称")
    private String functionName;

    @ApiModelProperty(value = "功能标识")
    private String functionId;

    @ApiModelProperty(value = "物联网设备id")
    private String iotId;

    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    @ApiModelProperty(value = "报警数据类型，0：老人异常数据，1：设备异常数据")
    private Integer alertDataType;

    @ApiModelProperty(value = "告警规则名称")
    private String alertRuleName;

    @ApiModelProperty(value = "运算符")
    private String operator;

    @ApiModelProperty(value = "阈值")
    private Double value;

    @ApiModelProperty(value = "持续周期")
    private Integer duration;

    @ApiModelProperty(value = "报警生效时段")
    private String alertEffectivePeriod;

    @ApiModelProperty(value = "报警沉默周期")
    private Integer alertSilentPeriod;

    @ApiModelProperty(value = "0禁用1启用")
    private Integer status;
}
