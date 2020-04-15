package com.github.mwierzchowski.helios.service.dto;

import lombok.Data;

@Data
public class TimerScheduleDto {
    private Integer id;
    private String time;
    private String[] days;
    private Boolean enabled;
}
