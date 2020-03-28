package com.github.mwierzchowski.helios.core.weather;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Info contributor for weather subdomain.
 * @author Marcin Wierzchowski
 */
@Component
public class WeatherInfoContributor implements InfoContributor {
    /**
     * Current weather observation published by weather publisher.
     */
    private Weather currentWeather = null;

    /**
     * Main contributor method.
     * @param builder info builder
     */
    @Override
    public synchronized void contribute(Info.Builder builder) {
        builder.withDetail("weather", currentWeather == null ? "unknown" : currentWeather);
    }

    @EventListener
    public synchronized void onWeatherObservation(WeatherObservationEvent weatherObservationEvent) {
        this.currentWeather = weatherObservationEvent.getCurrentWeather();
    }

    @EventListener
    public synchronized void onWeatherMissing(WeatherMissingEvent weatherMissingEvent) {
        this.currentWeather = null;
    }
}
