package com.github.mwierzchowski.helios.core.commons;

/**
 * SPI for component publishing local events.
 * @author Marcin Wierzchowski
 */
@FunctionalInterface
public interface EventStore {
    /**
     * Publish event
     * @param event event
     */
    void publish(HeliosEvent event);
}
