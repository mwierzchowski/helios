package com.github.mwierzchowski.helios.core.sun;

import lombok.Data;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * Ephemeris that provides time of sun events on given day.
 * @author Marcin Wierzchowski
 */
@Data
public class SunEphemeris {
    private LocalDate day;
    private Map<SunEphemerisEventType, LocalTime> times = new TreeMap<>();

    public Optional<SunEphemerisEvent> firstEventAfterNowOr(SunEphemerisEvent prevEvent, Clock clock) {
        var deadline = Optional.ofNullable(prevEvent).map(SunEphemerisEvent::getTimestamp)
                .orElseGet(clock::instant)
                .atZone(clock.getZone()).toLocalTime();
        return times.entrySet().stream()
                .filter(entry -> entry.getValue().isAfter(deadline))
                .map(Map.Entry::getKey)
                .sorted()
                .findFirst()
                .map(type -> eventOf(type, clock));
    }

    public SunEphemerisEvent firstEventOfDay(Clock clock) {
        var firstType = times.keySet().stream()
                .sorted()
                .findFirst()
                .orElseThrow();
        return eventOf(firstType, clock);
    }

    public SunEphemerisEvent eventOf(SunEphemerisEventType type, Clock clock) {
        Instant timestamp = times.get(type).atDate(day).atZone(clock.getZone()).toInstant();
        return new SunEphemerisEvent(type, timestamp);
    }
}
