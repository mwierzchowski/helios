package com.github.mwierzchowski.helios.core.commons;

import lombok.Data;

import java.time.Instant;

/**
 * Event published in case of failures
 * @author Marcin Wierzchowski
 */
@Data
public class FailureEvent implements HeliosEvent<Throwable> {
    /**
     * Component that send this event
     */
    private final String source;

    /**
     * Throwable of the failure
     */
    private final Throwable subject;

    /**
     * Timestamp of event
     */
    private final Instant timestamp = Instant.now();
}
