package com.github.mwierzchowski.helios.core.commons;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Interface for time-stamped application events.
 * @author Marcin Wierzchowski
 */
public interface TimestampedHeliosEvent extends HeliosEvent, Comparable<TimestampedHeliosEvent> {
    Instant getTimestamp();

    default ZonedDateTime getZonedDateTime() {
        return getTimestamp().atZone(ZoneId.systemDefault());
    }

    @Override
    default int compareTo(TimestampedHeliosEvent that) {
        var timestamp1 = this.getTimestamp().toEpochMilli();
        var timestamp2 = that.getTimestamp().toEpochMilli();
        return (int) (timestamp1 - timestamp2);
    }
}