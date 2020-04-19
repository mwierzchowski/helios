package com.github.mwierzchowski.helios.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

/**
 * DTO for timers.
 * @author Marcin Wierzchowski
 */
@Data
public class TimerDto {
    /**
     * Id of timer
     */
    @Schema(description = "Id of timer", example = "1", accessMode = READ_ONLY)
    private Integer id;

    /**
     * Description
     */
    @NotNull
    @Size(min = 1, max = 30)
    @Schema(description = "Unique description of timer", example = "Wake up")
    private String description;

    /**
     * Flag telling if timer is scheduled (contains any schedules).
     */
    @Schema(description = "Flag informing that timer has schedules", example = "true", accessMode = READ_ONLY)
    private Boolean scheduled;
}