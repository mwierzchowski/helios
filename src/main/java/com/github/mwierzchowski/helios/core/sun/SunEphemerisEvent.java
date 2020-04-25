package com.github.mwierzchowski.helios.core.sun;

import com.github.mwierzchowski.helios.core.commons.HeliosEvent;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import static java.time.Instant.now;

@Data
@RequiredArgsConstructor
public class SunEphemerisEvent implements HeliosEvent {
    private final SunEphemerisEventType type;
    private final Instant timestamp;

    public Duration getDelay(Clock clock) {
        return Duration.between(now(clock), timestamp);
    }
}
