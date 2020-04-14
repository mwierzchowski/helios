package com.github.mwierzchowski.helios.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimerScheduleDto {
    private Integer id;
    private String time;
    private String[] days;
    private Boolean enabled;
}
