package com.github.mwierzchowski.helios.core.weather;

import com.github.mwierzchowski.helios.core.commons.HeliosEvent;
import lombok.Data;

@Data
public class WeatherStaleEvent implements HeliosEvent<Weather> {
    /**
     * Stale weather observation
     */
    private final Weather subject;
}
