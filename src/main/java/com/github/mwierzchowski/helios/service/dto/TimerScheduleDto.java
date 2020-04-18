package com.github.mwierzchowski.helios.service.dto;

import lombok.Data;

/**
 * DTO for schedules.
 * @author Marcin Wierzchowski
 */
@Data
public class TimerScheduleDto {
    /**
     * Id of schedule
     */
    private Integer id;

    /**
     * Time of schedule
     */
    private String time;

    /**
     * Days when timer is scheduled
     */
    private String[] days;
}
