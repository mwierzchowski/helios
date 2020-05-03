package com.github.mwierzchowski.helios.adapter.owm;

import com.github.mwierzchowski.helios.adapter.commons.ExternalServiceHealthIndicator;
import com.github.mwierzchowski.helios.core.commons.Location;
import com.github.mwierzchowski.helios.core.commons.LocationProvider;
import com.github.mwierzchowski.helios.core.weather.Weather;
import com.github.mwierzchowski.helios.core.weather.WeatherProvider;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.openweathermap.api.CurrentWeatherApi;
import org.openweathermap.model.CurrentWeatherResponse;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.mapstruct.ReportingPolicy.IGNORE;

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
     * Mapper instance
     */
    private final static OwmMapper mapper = Mappers.getMapper(OwmMapper.class);

    /**
     * OWM properties
     */
    private final OwmProperties owmProperties;

    /**
     * Location provider
     */
    private final LocationProvider locationProvider;

    /**
     * OWM API client
     */
    private final CurrentWeatherApi weatherApi;

    /**
     * Health indicator for OWM adapter
     */
    private final ExternalServiceHealthIndicator<CurrentWeatherResponse> healthIndicator;

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
        Weather weather = mapper.toWeather(weatherResponse);
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

    /**
     * Mapper interface
     */
    @Mapper(unmappedTargetPolicy = IGNORE)
    interface OwmMapper {
        @Mapping(target = "timestamp", expression = "java(java.time.Instant.ofEpochSecond(response.getDt()))") // TODO
        @Mapping(target = "temperature.value", source = "response.main.temp")
        @Mapping(target = "temperature.unit", constant = "CELSIUS") // TODO
        @Mapping(target = "wind.speed.value", source ="response.wind.speed")
        @Mapping(target = "wind.speed.unit", constant = "METERS_PER_SECOND") // TODO
        @Mapping(target = "wind.direction", source ="response.wind.deg")
        @Mapping(target = "cloudsCoverage", source ="response.clouds.all")
        Weather toWeather(CurrentWeatherResponse response);
    }
}
