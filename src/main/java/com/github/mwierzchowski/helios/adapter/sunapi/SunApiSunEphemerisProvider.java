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

@Slf4j
@Component
@RequiredArgsConstructor
public class SunApiSunEphemerisProvider implements SunEphemerisProvider {
    /**
     * SunApi properties
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

    public SunEphemeris missingEphemeris(Throwable throwable) {
        log.error("Ephemeris request failed, providing fallback", throwable);
        healthIndicator.register(throwable);
        return sunProperties.getFallback().getSunEphemeris();
    }

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

    private LocalTime toLocalTime(OffsetDateTime odt) {
        return odt.atZoneSameInstant(ZoneId.systemDefault()).toLocalTime();
    }
}
