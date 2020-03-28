package com.github.mwierzchowski.helios.core.weather;

import com.github.mwierzchowski.helios.HeliosProperties;
import com.github.mwierzchowski.helios.core.HeliosEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

/**
 * Component responsible for periodical weather conditions checking and publishing them if conditions change.
 * @author Marcin Wierzchowski
 */
@Component
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class WeatherPublisher {
    /**
     * Application properties
     */
    private final HeliosProperties heliosProperties;

    /**
     * Weather provider
     */
    private final WeatherProvider weatherProvider;

    /**
     * Events publisher
     */
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Last published event or null when its not available
     */
    private HeliosEvent lastEvent;

    /**
     * Scheduled method that executes weather check and publish event. New weather event is published when conditions
     * have changed. Otherwise no event is published.
     */
    @Scheduled(fixedDelayString = "#{heliosProperties.weather.checkInterval}")
    public void publishWeather() {
        weatherProvider.currentWeather()
                .map(this::toWeatherNotification)
                .orElseGet(this::missingNotification)
                .ifPresent(this::send);
    }

    /** Helper methods ************************************************************************************************/

    private Optional<HeliosEvent> toWeatherNotification(Weather currentWeather) {
        if (!currentWeather.isDifferentThan(previousWeather())) {
            log.debug("Weather has not changed.");
            return Optional.empty();
        }
        log.debug("Weather has changed. New observation: {}", currentWeather);
        HeliosEvent event = new WeatherObservationEvent(currentWeather);
        return Optional.of(event);
    }

    private Optional<HeliosEvent> missingNotification() {
        if (lastEventWasWarning()) {
            log.debug("Missing weather warning was already sent earlier");
            return Optional.empty();
        }
        Instant deadline = Instant.now().minusMillis(heliosProperties.getWeather().getObservationDeadline());
        Weather previousWeather = previousWeather();
        if (previousWeather != null && previousWeather.getTimestamp().isAfter(deadline)) {
            log.debug("Weather observation is missing but warning deadline has not been passed yet");
            return Optional.empty();
        }
        log.error("Weather observation is missing for {}s", heliosProperties.getWeather().getObservationDeadline() / 1000);
        HeliosEvent event = new WeatherMissingEvent();
        return Optional.of(event);
    }

    private void send(HeliosEvent event) {
        eventPublisher.publishEvent(event);
        this.lastEvent = event;
    }

    private Weather previousWeather() {
        if (lastEventWasWarning() || lastEvent == null) {
            return null;
        } else {
            return ((WeatherObservationEvent) lastEvent).getCurrentWeather();
        }
    }

    private boolean lastEventWasWarning() {
        return lastEvent != null && lastEvent instanceof WeatherMissingEvent;
    }
}