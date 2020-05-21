package com.github.mwierzchowski.helios.core.commons;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Marker interface for application events
 * @author Marcin Wierzchowski
 */
public interface HeliosEvent<T> extends Comparable<HeliosEvent<T>> {
    default String getSource() {
        return null;
    }

    default T getSubject() {
        return null;
    }

    default Instant getTimestamp() {
        return null;
    }

    default ZonedDateTime getZonedDateTime() {
        var timestamp = getTimestamp();
        return timestamp == null ? null : timestamp.atZone(ZoneId.systemDefault());
    }

    @Override
    default int compareTo(HeliosEvent that) {
        var timestamp1 = this.getTimestamp().toEpochMilli();
        var timestamp2 = that.getTimestamp().toEpochMilli();
        return (int) (timestamp1 - timestamp2);
    }
}
