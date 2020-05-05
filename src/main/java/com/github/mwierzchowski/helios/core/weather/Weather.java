package com.github.mwierzchowski.helios.core.weather;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static java.util.Optional.ofNullable;

/**
 * Represents weather conditions outside.
 * @author Marcin Wierzchowski
 */
@Data
public class Weather {
    /**
     * List of sources that provided this weather conditions
     */
    @NotNull
    @Size(min = 1)
    private List<String> sources = new ArrayList<>();

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

    /**
     * Adds given source to list of sources
     * @param source weather source
     */
    public void setSource(String source) {
        this.sources.add(source);
    }

    /**
     * Checks if given weather observation is same as other. Timestamp is ignored as its irrelevant in the
     * context of weather observation.
     * @param other weather
     * @return true if weather observations are the same, otherwise false
     */
    public boolean isSameAs(Weather other) {
        return other != null
                && this.temperature.equals(other.temperature)
                && this.wind.equals(other.wind)
                && this.cloudsCoverage.equals(other.cloudsCoverage);
    }

    /**
     * Updates given weather with other weather conditions.
     * @param other weather to update given one
     */
    public void update(Weather other) {
        if (other == null) {
            return;
        }
        this.sources.addAll(other.sources);
        ofNullable(other.timestamp).ifPresent(this::setTimestamp);
        ofNullable(other.temperature).ifPresent(this::setTemperature);
        ofNullable(other.wind).ifPresent(this::setWind);
        ofNullable(other.cloudsCoverage).ifPresent(this::setCloudsCoverage);
    }

    /**
     * Tells if weather was provided by some sources. Otherwise its empty (or with default values).
     * @return true if weather was provided
     */
    public boolean isProvided() {
        return !this.sources.isEmpty();
    }
}
