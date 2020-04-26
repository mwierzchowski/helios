package com.github.mwierzchowski.helios.core.commons;

import java.time.Instant;

/**
 * Interface for time-stamped application events.
 * @author Marcin Wierzchowski
 */
public interface TimestampedHeliosEvent extends HeliosEvent, Comparable<TimestampedHeliosEvent> {
    Instant getTimestamp();

    @Override
    default int compareTo(TimestampedHeliosEvent other) {
        var timestamp1 = this.getTimestamp().toEpochMilli();
        var timestamp2 = other.getTimestamp().toEpochMilli();
        return (int) (timestamp1 - timestamp2);
    }
}