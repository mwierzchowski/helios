package com.github.mwierzchowski.helios.core.sun;

import com.github.mwierzchowski.helios.core.commons.HeliosEvent;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import static java.time.Instant.now;

/**
 * Sun ephemeris event
 * @author Marcin Wierzchowski
 */
@Data
@RequiredArgsConstructor
public class SunEphemerisEvent implements HeliosEvent<SunEphemerisType> {
    /**
     * Type of event
     */
    private final SunEphemerisType subject;

    /**
     * Timestamp of the event (e.g. when sunrise happens).
     */
    private final Instant timestamp;

    /**
     * Calculates duration between now and event timestamp
     * @param clock clock
     * @return duration
     */
    public Duration getDelay(Clock clock) {
        return Duration.between(now(clock), timestamp);
    }
}
