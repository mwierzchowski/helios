package com.github.mwierzchowski.helios.core.weather;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Represents speed value in given units (see {@link SpeedUnit}).
 * @author Marcin Wierzchowski
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Speed {
    /**
     * Speed value.
     */
    @NotNull
    @Min(0)
    private BigDecimal value;

    /**
     * Speed unit (see {@link SpeedUnit}).
     */
    @NotNull
    private SpeedUnit unit;
}
