package com.github.mwierzchowski.helios.core.commons;

/**
 * Functional interface for obtaining geographical location. It is assumed that location does not change during
 * application lifetime but its up to implementation to cache location or calculate it on each call.
 * @author Marcin Wierzchowski
 */
@FunctionalInterface
public interface LocationProvider {
    /**
     * Obtains location.
     * @return location
     */
    Location locate();
}
