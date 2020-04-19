package com.github.mwierzchowski.helios.service.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

/**
 * DTO for schedules.
 * @author Marcin Wierzchowski
 */
@Data
public class TimerScheduleDto {
    /**
     * Id of schedule
     */
    @Schema(description = "Id of schedule", example = "1", accessMode = READ_ONLY)
    private Integer id;

    /**
     * Time of schedule
     */
    @NotNull
    @Schema(description = "Timer's schedule time in ISO format", example = "06:30:00")
    private String time;

    /**
     * Days when timer is scheduled
     */
    @Size(min = 1, max = 7)
    @ArraySchema(uniqueItems = true, schema = @Schema(
            description = "English day names in uppercase when timer is scheduled.",
            allowableValues = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"})
    )
    private String[] days;
}
