package com.github.mwierzchowski.helios.core.sun;

import java.time.LocalDate;

/**
 * SPI for Sun ephemeris providers.
 * @author Marcin Wierzchowski
 */
@FunctionalInterface
public interface SunEphemerisProvider {
    /**
     * Provide sun ephemeris for given day.
     * @return ephemeris
     */
    SunEphemeris sunEphemerisFor(LocalDate day);
}
