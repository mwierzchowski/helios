package com.github.mwierzchowski.helios;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Helios application properties.
 * @author Marcin Wierzchowski
 */
@Data
@ConfigurationProperties("helios")
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
    private LocationProperties location;

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

    /**
     * Open Weather Map properties
     */
    private OwmProperties owm = new OwmProperties();

    @Data
    public static class OwmProperties {
        /**
         * OpenWeatherMap service base path
         */
        private String basePath = "http://api.openweathermap.org/data/2.5";

        /**
         * OpenWeatherMap service api key. Please signup for free subscription on https://home.openweathermap.org/users/sign_up.
         * For rules of free subscription please take a look on https://openweathermap.org/price#commonquestions.
         */
        private String apiKey = "REGISTER_FOR_API_KEY";
    }
}
