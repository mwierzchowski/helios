package com.github.mwierzchowski.helios.adapter.sunapi;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

/**
 * Sun API adapter configuration.
 * @author Marcin Wierzchowski
 */
@Data
@Component
@ConfigurationProperties("helios.sunapi")
public class SunApiProperties {
    /**
     * Base path of the Sunrise Sunset service.
     */
    private String basePath = "https://api.sunrise-sunset.org:443";

    /**
     * Cron expression for running process that cache next days ephemeris data
     */
    private String cacheCron = "0 0 0 * * *"; // every day at 00:00:90

    /**
     * Number of next days to be cached
     */
    private Integer cacheDays = 7;

    /**
     * Fallback properties
     */
    @NestedConfigurationProperty
    private SunApiFallbackProperties fallback = new SunApiFallbackProperties();
}
