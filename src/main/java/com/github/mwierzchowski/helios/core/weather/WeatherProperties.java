package com.github.mwierzchowski.helios.core.weather;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Weather properties.
 * @author Marcin Wierzchowski
 */
@Data
@Component
@ConfigurationProperties("helios.weather")
public class WeatherProperties {
    /**
     * Interval in ms for checking weather conditions.
     */
    private Long checkInterval = 60000L; // 1 min

    /**
     * Deadline in ms for weather observation to be available. After that time, warning will be issued.
     */
    private Long observationDeadline = 15L * 60000; // 15 mins
}
