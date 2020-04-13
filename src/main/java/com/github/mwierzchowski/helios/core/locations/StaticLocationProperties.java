package com.github.mwierzchowski.helios.core.locations;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Static location properties.
 * @author Marcin Wierzchowski
 */
@Data
@Component
@ConfigurationProperties("helios.location")
public class StaticLocationProperties {
    /**
     * City
     */
    private String city;

    /**
     * Geographical latitude
     */
    private Double latitude;

    /**
     * Geographical longitude
     */
    private Double longitude;
}
