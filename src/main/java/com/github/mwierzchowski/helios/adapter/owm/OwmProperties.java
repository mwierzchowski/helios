package com.github.mwierzchowski.helios.adapter.owm;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Open Weather Map properties.
 * @author Marcin Wierzchowski
 */
@Data
@Component
@ConfigurationProperties("helios.owm")
public class OwmProperties {
    /**
     * Open Weather Map service base path
     */
    private String basePath = "http://api.openweathermap.org/data/2.5";

    /**
     * Open Weather Map service api key. Please sign up for free subscription on
     * https://home.openweathermap.org/users/sign_up. For rules of free subscription please take a look on
     * https://openweathermap.org/price#commonquestions.
     */
    private String apiKey;

    /**
     * Language
     */
    private String language = "pl";

    /**
     * Units system
     */
    private String unitsSystem = "metric";

    /**
     * Time to live for cached responses. OWM provides new observations  at earliest every 10 mins, so shorter TTL
     * does not make sense.
     */
    private Long cacheTtl = 900000L; // 15 min
}
