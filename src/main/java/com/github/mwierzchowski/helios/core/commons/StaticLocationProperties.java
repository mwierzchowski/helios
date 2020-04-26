package com.github.mwierzchowski.helios.core.commons;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static java.util.Optional.ofNullable;

/**
 * Static location properties.
 * @author Marcin Wierzchowski
 */
@Data
@Slf4j
@Component
@ConfigurationProperties("helios.location")
@ConditionalOnProperty(prefix = "helios.location", name = {"latitude", "longitude"})
public class StaticLocationProperties implements LocationProvider {
    /**
     * Default city name to be used when city was not provided.
     */
    private String defaultCity = "unknown city";

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

    /**
     * Callback method for reporting location on application start.
     */
    @PostConstruct
    public void reportLocation() {
        log.info("Static location is {} (lat={}, lon={})", cityOrDefault(), latitude, longitude);
    }

    /**
     * Main business method
     * @return location
     */
    @Override
    @Cacheable("static-location")
    public Location locate() {
        return new Location(cityOrDefault(), latitude, longitude);
    }

    private String cityOrDefault() {
        return ofNullable(city).orElse(defaultCity);
    }
}
