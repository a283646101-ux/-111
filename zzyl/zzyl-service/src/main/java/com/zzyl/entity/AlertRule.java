package com.zzyl.entity;

import com.zzyl.base.BaseEntity;
import lombok.Data;

@Data
public class AlertRule extends BaseEntity {

    private String productKey;

    private String productName;

    private String moduleId;

    private String moduleName;

    private String functionName;

    private String functionId;

    private String iotId;

    private String deviceName;

    private Integer alertDataType;

    private String alertRuleName;

    private String operator;

    private Double value;

    private Integer duration;

    private String alertEffectivePeriod;

    private Integer alertSilentPeriod;

    private Integer status;
}
