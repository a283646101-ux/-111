package com.zzyl.entity;

import com.zzyl.base.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DeviceData extends BaseEntity {

    private String deviceName;

    private String iotId;

    private String noteName;

    private String productId;

    private String productName;

    private String functionName;

    private String accessLocation;

    private String dataValue;

    private LocalDateTime alarmTime;

    private String processingResult;

    private String processor;

    private LocalDateTime processingTime;

    private String status;
}
