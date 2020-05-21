package com.github.mwierzchowski.helios.core.weather;

import com.github.mwierzchowski.helios.core.commons.HeliosEvent;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Event for observation observations.
 * @author Marcin Wierzchowski
 */
@Data
@RequiredArgsConstructor
public class WeatherObservationEvent implements HeliosEvent<Void> {
    /**
     * Current weather observation
     */
    private final Weather currentWeather;
}
