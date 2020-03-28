package com.github.mwierzchowski.helios.core;

import lombok.Data;

import java.time.Instant;

/**
 * Parent class for all application events.
 * @author Marcin Wierzchowski
 */
@Data
public abstract class HeliosEvent {
    /**
     * Timestamp of this event creation
     */
    private final Instant timestamp = Instant.now();
}
