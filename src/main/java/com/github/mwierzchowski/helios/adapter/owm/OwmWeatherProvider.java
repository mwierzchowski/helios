package com.github.mwierzchowski.helios.adapter.owm;

import com.github.mwierzchowski.helios.core.locations.Location;
import com.github.mwierzchowski.helios.core.locations.LocationProvider;
import com.github.mwierzchowski.helios.core.weather.Weather;
import com.github.mwierzchowski.helios.core.weather.WeatherProvider;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openweathermap.api.CurrentWeatherApi;
import org.openweathermap.model.CurrentWeatherResponse;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Open Weather Map (OWM) implementation of the weather source. Responses from OWM are cached to avoid running over the
 * queries limit. Additionally, frequent calls do not make sense, since OWM provides current weather every 10 mins at
 * most. If needed, cache could be manually expired by calling {@link OwmWeatherProvider#expireCachedResponse()} method.
 * In case of communication issues, call to OWM API is retried number of times. At the end, empty {@link Optional} is
 * returned.
 * @author Marcin Wierzchowski
 */
@Component
@CacheConfig(cacheNames = {"owm-weather-source"})
@Slf4j
@RequiredArgsConstructor
public class OwmWeatherProvider implements WeatherProvider {
    /**
     * OWM properties.
     */
    private final OwmProperties owmProperties;

    /**
     * Location provider.
     */
    private final LocationProvider locationProvider;

    /**
     * OWM API client.
     */
    private final CurrentWeatherApi weatherApi;

    /**
     * OWM domain mapper.
     */
    private final OwmDomainMapper domainMapper;

    /**
     * Health indicator for OWM adapter.
     */
    private final OwmHealthIndicator healthIndicator;

    @Override
    @Cacheable
    @Retry(name = "owm", fallbackMethod = "missingWeather")
    public Optional<Weather> currentWeather() {
        Location location = locationProvider.locate();
        CurrentWeatherResponse weatherResponse = weatherApi.currentWeather(
                location.getLatitude(),
                location.getLongitude(),
                owmProperties.getUnitsSystem(),
                owmProperties.getLanguage()
        );
        log.debug("Current weather response: {}", weatherResponse);
        Weather weather = domainMapper.toWeather(weatherResponse);
        healthIndicator.register(weatherResponse);
        return Optional.of(weather);
    }

    /**
     * Fallback method used when its not possible to provide current weather due to technical issues with OWM.
     * @param throwable problem root cause
     * @return empty optional
     */
    public Optional<Weather> missingWeather(Throwable throwable) {
        log.error("Current weather request failed", throwable);
        healthIndicator.register(throwable);
        return Optional.empty();
    }

    /**
     * Scheduled method that expires response cache. Should be called at least every 10 mins or more since OWM collects
     * weather info every 10 mins at most.
     */
    @Scheduled(
            fixedRateString = "#{owmProperties.cacheTtl}",
            initialDelayString = "#{owmProperties.cacheTtl}"
    )
    @CacheEvict(allEntries = true)
    public void expireCachedResponse() {
        log.debug("Cached weather response (if any) expired");
    }
}
