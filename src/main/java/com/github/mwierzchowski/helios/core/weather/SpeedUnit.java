package com.github.mwierzchowski.helios.core.weather;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

/**
 * Enumeration that represents speed unit.
 * @author Marcin Wierzchowski
 */
@AllArgsConstructor
public enum SpeedUnit {
    KILOMETERS_PER_HOUR("km/h"),
    METERS_PER_SECOND("m/s"),
    MILES_PER_HOUR("mph");

    /**
     * Symbol of unit.
     */
    @Getter
    private String symbol;

    /**
     * Provides enumeration for given symbol.
     * @param symbol speed unit symbol
     * @return SpeedUnit
     * @throws IllegalArgumentException when unit is not supported
     */
    public static SpeedUnit ofSymbol(String symbol) {
        return Stream.of(SpeedUnit.values())
                .filter(unit -> unit.symbol.equals(symbol))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Speed unit '" + symbol + "' is not supported"));
    }
}
