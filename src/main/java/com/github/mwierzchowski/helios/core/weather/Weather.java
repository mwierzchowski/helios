package com.github.mwierzchowski.helios.core.weather;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.Instant;

/**
 * Represents weather conditions outside.
 * @author Marcin Wierzchowski
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Weather {
    /**
     * Timestamp of weather observation.
     */
    @NotNull
    @PastOrPresent
    private Instant timestamp;

    /**
     * Temperature
     */
    @NotNull
    @Valid
    private Temperature temperature;

    /**
     * Wind
     */
    @NotNull
    @Valid
    private Wind wind;

    /**
     * Clouds coverage percentage. 0 when there are no clouds, 100% sky fully covered.
     */
    @NotNull
    @Min(0)
    @Max(100)
    private Integer cloudsCoverage;
}
