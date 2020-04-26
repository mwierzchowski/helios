package com.github.mwierzchowski.helios.core.commons;

import java.time.Instant;

/**
 * Interface for time-stamped application events.
 * @author Marcin Wierzchowski
 */
public interface TimestampedHeliosEvent extends HeliosEvent {
    Instant getTimestamp();
}
