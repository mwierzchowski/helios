package com.github.mwierzchowski.helios.core.commons;

import lombok.Data;

import java.time.Instant;

/**
 * Event published in case of failures
 * @author Marcin Wierzchowski
 */
@Data
public class FailureEvent implements TimestampedHeliosEvent {
    /**
     * Timestamp of event
     */
    private final Instant timestamp = Instant.now();

    /**
     * Component that send this event
     */
    private final Class<?> source;

    /**
     * Throwable of the failure
     */
    private final Throwable throwable;
}
