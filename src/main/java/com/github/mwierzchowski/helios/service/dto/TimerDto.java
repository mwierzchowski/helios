package com.github.mwierzchowski.helios.service.dto;

import lombok.Data;

/**
 * DTO for timers.
 * @author Marcin Wierzchowski
 */
@Data
public class TimerDto {
    /**
     * Id of timer
     */
    private Integer id;

    /**
     * Description
     */
    private String description;

    /**
     * Flag telling if timer is scheduled (contains any schedules).
     */
    private Boolean scheduled;
}