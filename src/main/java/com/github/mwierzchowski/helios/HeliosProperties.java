package com.github.mwierzchowski.helios;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Helios application properties.
 * @author Marcin Wierzchowski
 */
@Data
@Component
@ConfigurationProperties(prefix = "helios")
public class HeliosProperties {
    /**
     * Language
     */
    private String language = "pl";

    /**
     * Units system
     */
    private String unitsSystem = "metric";

    /**
     * Static location
     */
    @Data
    public static class LocationProperties {
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
    private LocationProperties location = new LocationProperties();

    /**
     * Weather properties
     */
    @Data
    public static class WeatherProperties {
        /**
         * Interval in ms for checking weather conditions.
         */
        private Long checkInterval = 60000L; // 1 min

        /**
         * Deadline in ms for weather observation to be available. After that time, warning will be issued.
         */
        private Long observationDeadline = 15L * 60000; // 15 mins
    }
    private WeatherProperties weather = new WeatherProperties();

    /**
     * Open Weather Map properties
     */
    @Data
    public static class OwmProperties {
        /**
         * OpenWeatherMap service base path
         */
        private String basePath = "http://api.openweathermap.org/data/2.5";

        /**
         * OpenWeatherMap service api key. Please sign up for free subscription on
         * https://home.openweathermap.org/users/sign_up. For rules of free subscription please take a look on
         * https://openweathermap.org/price#commonquestions.
         */
        private String apiKey = "REGISTER_FOR_API_KEY";

        /**
         * Time to live for cached responses. OWM provides new observations  at earliest every 10 mins, so shorter TTL
         * does not make sense.
         */
        private Long cacheTtl = 900000L; // 15 min
    }
    private OwmProperties owm = new OwmProperties();
}
