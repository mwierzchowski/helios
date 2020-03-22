package com.github.mwierzchowski.helios.core.weather;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Represents temperature value in given units (see {@link TemperatureUnit}).
 *
 * @author Marcin Wierzchowski
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Temperature {
    /**
     * Temperature value.
     */
    @NotNull
    private BigDecimal value;

    /**
     * Temperature unit (see {@link TemperatureUnit}).
     */
    @NotNull
    private TemperatureUnit unit;
}
