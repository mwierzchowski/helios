package com.github.mwierzchowski.helios.core.commons;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Immutable object representing geographical location.
 * @author Marcin Wierzchowski
 */
@Data
public class Location {
    /**
     * City name. Could be null.
     */
    private final String city;

    /**
     * Geographical latitude. Can not be null.
     */
    @NotNull
    private final Double latitude;

    /**
     * Geographical longitude. Can not be null.
     */
    @NotNull
    private final Double longitude;
}
