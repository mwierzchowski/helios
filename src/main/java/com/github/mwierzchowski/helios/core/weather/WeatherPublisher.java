package com.github.mwierzchowski.helios.core.weather;

import com.github.mwierzchowski.helios.core.commons.EventStore;
import com.github.mwierzchowski.helios.core.commons.HeliosEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Component responsible for periodical weather conditions checking and publishing them if conditions change.
 * @author Marcin Wierzchowski
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class WeatherPublisher {
    /**
     * Weather properties
     */
    private final WeatherProperties weatherProperties;

    /**
     * Weather providers
     */
    private final List<WeatherProvider> weatherProviders;

    /**
     * Events store
     */
    private final EventStore eventStore;

    /**
     * Last published event or null when its not available
     */
    private HeliosEvent<Weather> lastEvent;

    @PostConstruct
    public void initialize() {
        if (providersNotAvailable()) {
            log.warn("Providers are not available, weather will not be published");
        }
    }

    /**
     * Scheduled method that executes weather check and publish event. New weather event is published when conditions
     * have changed. Otherwise no event is published.
     */
    @Scheduled(fixedRateString = "#{weatherProperties.checkInterval}",
            initialDelayString = "#{weatherProperties.checkDelayAfterStartup}")
    public void publishWeather() {
        if (providersNotAvailable()) {
            return;
        }
        Weather currentWeather = new Weather();
        for (var weatherProvider :  weatherProviders) {
            weatherProvider.currentWeather().ifPresent(currentWeather::update);
        }
        Optional<HeliosEvent<Weather>> event;
        if (currentWeather.isProvided()) {
            event = weatherNotification(currentWeather);
        } else {
            event = missingNotification();
        }
        event.ifPresent(this::send);
    }

    private Optional<HeliosEvent<Weather>> weatherNotification(Weather currentWeather) {
        if (currentWeather.isSameAs(previousWeather())) {
            log.debug("Weather has not changed.");
            return Optional.empty();
        }
        log.debug("Weather has changed. New observation: {}", currentWeather);
        HeliosEvent<Weather> event = new WeatherObservationEvent(currentWeather);
        return Optional.of(event);
    }

    private Optional<HeliosEvent<Weather>> missingNotification() {
        if (lastEventWasWarning()) {
            log.debug("Missing weather warning was already sent earlier");
            return Optional.empty();
        }
        Instant deadline = Instant.now().minusMillis(weatherProperties.getObservationDeadline());
        Weather previousWeather = previousWeather();
        if (previousWeather != null && previousWeather.getTimestamp().isAfter(deadline)) {
            log.debug("Weather observation is missing but warning deadline has not been passed yet");
            return Optional.empty();
        }
        log.error("Weather observation is stale");
        HeliosEvent<Weather> event = new WeatherStaleEvent(previousWeather);
        return Optional.of(event);
    }

    private void send(HeliosEvent<Weather> event) {
        eventStore.publish(event);
        this.lastEvent = event;
    }

    private Weather previousWeather() {
        if (lastEventWasWarning() || lastEvent == null) {
            return null;
        } else {
            return lastEvent.getSubject();
        }
    }

    private boolean lastEventWasWarning() {
        return lastEvent instanceof WeatherStaleEvent;
    }

    private boolean providersNotAvailable() {
        return weatherProviders == null || weatherProviders.isEmpty();
    }
}