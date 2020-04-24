package com.github.mwierzchowski.helios.core.commons;

@FunctionalInterface
public interface EventStore {
    void publish(HeliosEvent event);
}
