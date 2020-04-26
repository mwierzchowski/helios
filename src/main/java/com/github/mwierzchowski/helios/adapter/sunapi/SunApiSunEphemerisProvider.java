package com.github.mwierzchowski.helios.adapter.sunapi;

import com.github.mwierzchowski.helios.adapter.commons.ExternalServiceHealthIndicator;
import com.github.mwierzchowski.helios.core.commons.LocationProvider;
import com.github.mwierzchowski.helios.core.sun.SunEphemeris;
import com.github.mwierzchowski.helios.core.sun.SunEphemerisProvider;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.sunrisesunset.api.SunriseSunsetApi;
import org.sunrisesunset.model.SunriseSunsetResponse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.github.mwierzchowski.helios.core.sun.SunEphemerisEventType.Dawn;
import static com.github.mwierzchowski.helios.core.sun.SunEphemerisEventType.Dusk;
import static com.github.mwierzchowski.helios.core.sun.SunEphemerisEventType.Noon;
import static com.github.mwierzchowski.helios.core.sun.SunEphemerisEventType.Sunrise;
import static com.github.mwierzchowski.helios.core.sun.SunEphemerisEventType.Sunset;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

/**
 * Implementation of {@link SunEphemerisProvider} that provides {@link SunEphemeris} from https://sunrise-sunset.org/
 * service.
 * @author Marcin Wierzchowski
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SunApiSunEphemerisProvider implements SunEphemerisProvider {
    /**
     * Sun Api properties
     */
    private final SunApiProperties sunProperties;

    /**
     * Location provider
     */
    private final LocationProvider locationProvider;

    /**
     * SunApi API client
     */
    private final SunriseSunsetApi api;

    /**
     * Health indicator
     */
    private final ExternalServiceHealthIndicator<SunriseSunsetResponse> healthIndicator;

    @Getter
    private final Map<LocalDate, SunEphemeris> cache = new ConcurrentHashMap<>();

    /**
     * Populates cache with configured number of future sun ephemeris and removes old (from past) data. Method should be
     * called on application startup and everyday on the same time (configured by cron). In case of communication
     * issues method is automatically retried but should not block calling scheduler thread.
     */
    @Retry(name = "sun-api-cache")
    @Scheduled(cron = "#{sunApiProperties.cacheCron}")
    @Async
    @EventListener(classes = ApplicationReadyEvent.class, condition = "@commonProperties.processingOnStartupEnabled")
    public void manageCache() {
        log.debug("Caching ephemeris for next {} days", sunProperties.getCacheDays());
        var today = LocalDate.now();
        for (int i = 0; i < sunProperties.getCacheDays(); i++) {
            sunEphemerisFor(today.plusDays(i));
        }
        var outdatedEntries = cache.keySet().stream().filter(today::isAfter).collect(toList());
        log.debug("Removing from cache {} outdated entries", outdatedEntries.size());
        outdatedEntries.forEach(cache::remove);
    }

    /**
     * Main provider method. Calls Sun API service and stores result in cache. It returns value from cache if it
     * already contains ephemeris for given day. In case of communication issues, call is retried according to the
     * configuration. In the worst case scenario, fallback method provides configured fallback ephemeris (but its not
     * stored in the cache).
     * @param day day for which ephemeris should be calculated
     * @return ephemeris
     */
    @Override
    @Retry(name = "sun-api", fallbackMethod = "missingEphemeris")
    public SunEphemeris sunEphemerisFor(LocalDate day) {
        var ephemeris = cache.get(day);
        if (ephemeris != null) {
            log.debug("Ephemeris for {} is available in cache", day);
            return ephemeris;
        }
        log.info("Requesting ephemeris for {} ", day);
        var location = locationProvider.locate();
        var sunApiResponse = api.sunriseSunset(
                location.getLatitude(),
                location.getLongitude(),
                day.toString(),
                0
        );
        log.debug("Sun API response: {}", sunApiResponse);
        ephemeris = toSunEphemeris(sunApiResponse);
        healthIndicator.register(sunApiResponse);
        cache.putIfAbsent(day, ephemeris);
        return ephemeris;
    }

    /**
     * Fallback method that provides ephemeris from properties
     * @param throwable error
     * @return fallback ephmeris
     */
    public SunEphemeris missingEphemeris(Throwable throwable) {
        log.error("Ephemeris request failed, providing fallback", throwable);
        healthIndicator.register(throwable);
        return sunProperties.getFallback().getSunEphemeris();
    }

    /**
     * Helper method that mapps Sun API response to {@link SunEphemeris}.
     * @param sunApiResponse service response
     * @return sun ephemeris
     */
    private SunEphemeris toSunEphemeris(SunriseSunsetResponse sunApiResponse) {
        SunEphemeris ephemeris = new SunEphemeris();
        ephemeris.setDay(sunApiResponse.getResults().getSolarNoon()
                .atZoneSameInstant(ZoneId.systemDefault())
                .toLocalDate());
        ofNullable(sunApiResponse.getResults().getCivilTwilightBegin()).map(this::toLocalTime)
                .ifPresent(time -> ephemeris.getTimes().put(Dawn, time));
        ofNullable(sunApiResponse.getResults().getSunrise()).map(this::toLocalTime)
                .ifPresent(time -> ephemeris.getTimes().put(Sunrise, time));
        ofNullable(sunApiResponse.getResults().getSolarNoon()).map(this::toLocalTime)
                .ifPresent(time -> ephemeris.getTimes().put(Noon, time));
        ofNullable(sunApiResponse.getResults().getSunset()).map(this::toLocalTime)
                .ifPresent(time -> ephemeris.getTimes().put(Sunset, time));
        ofNullable(sunApiResponse.getResults().getCivilTwilightEnd()).map(this::toLocalTime)
                .ifPresent(time -> ephemeris.getTimes().put(Dusk, time));
        return ephemeris;
    }

    /**
     * Helper method that maps {@link OffsetDateTime} to {@link LocalTime}
     * @param odt offset date time
     * @return local time
     */
    private LocalTime toLocalTime(OffsetDateTime odt) {
        return odt.atZoneSameInstant(ZoneId.systemDefault()).toLocalTime();
    }
}
