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
    /**
     * Day of ephemeris
     */
    private LocalDate day;

    /**
     * Map with time of ephemeris events
     */
    private Map<SunEphemerisEventType, LocalTime> times = new TreeMap<>();

    /**
     * Flag informing if ephemeris is approximated. It is set to true in case of fallback response from
     * {@link SunEphemerisProvider}.
     */
    private Boolean approximated = false;

    /**
     * Finds first ephemeris event that happens after given event or (if event is null) after current timestamp.
     * @param prevEvent previous event, could be null
     * @param clock clock
     * @return event
     */
    public Optional<SunEphemerisEvent> firstEventAfterPreviousOrNow(SunEphemerisEvent prevEvent, Clock clock) {
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

    /**
     * Finds first event of the day
     * @param clock clock
     * @return event
     */
    public SunEphemerisEvent firstEventOfDay(Clock clock) {
        var firstType = times.keySet().stream()
                .sorted()
                .findFirst()
                .orElseThrow();
        return eventOf(firstType, clock);
    }

    /**
     * Helper method that provides event of the given type
     * @param type type of event
     * @param clock clock
     * @return event
     */
    private SunEphemerisEvent eventOf(SunEphemerisEventType type, Clock clock) {
        Instant timestamp = times.get(type).atDate(day).atZone(clock.getZone()).toInstant();
        return new SunEphemerisEvent(type, timestamp);
    }
}
