package com.github.mwierzchowski.helios.core.weather;

import com.github.mwierzchowski.helios.core.HeliosEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * Event for observation observations.
 * @author Marcin Wierzchowski
 */
@Data
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class WeatherObservationEvent extends HeliosEvent {
    /**
     * Current weather observation
     */
    private final Weather currentWeather;
}
