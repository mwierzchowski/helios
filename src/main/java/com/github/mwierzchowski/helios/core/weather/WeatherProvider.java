package com.github.mwierzchowski.helios.core.weather;

import java.util.Optional;

/**
 * Interface for weather providers.
 * @author Marcin Wierzchowski
 */
@FunctionalInterface
public interface WeatherProvider {
    /**
     * Provide current weather conditions.
     * @return current weather conditions
     */
    Optional<Weather> currentWeather();
}
