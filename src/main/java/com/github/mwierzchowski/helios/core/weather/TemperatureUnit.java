package com.github.mwierzchowski.helios.core.weather;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.stream.Stream;

/**
 * Enumeration that represents temperature units.
 * @author Marcin Wierzchowski
 */
@AllArgsConstructor
public enum TemperatureUnit {
    CELSIUS('C'),
    FAHRENHEIT('F'),
    KELVIN('K');

    /**
     * Upper case symbol of the unit.
     */
    @Getter
    private Character symbol;

    /**
     * Provides enumeration for given symbol.
     * @param symbol temperature unit symbol
     * @return TemperatureUnit
     * @throws IllegalArgumentException when unit symbol is lower case
     * @throws IllegalArgumentException when unit is not supported
     */
    public static TemperatureUnit ofSymbol(@NonNull Character symbol) {
        return Stream.of(TemperatureUnit.values())
                .filter(unit -> unit.symbol.equals(symbol))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Temperature unit '" + symbol + "' is not supported"));
    }
}
