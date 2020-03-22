package com.github.mwierzchowski.helios.core.weather;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Represents wind vector made of speed and direction.
 *
 * @author Marcin Wierzchowski
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Wind {
    /**
     * Speed of wind.
     */
    @NotNull
    @Valid
    private Speed speed;

    /**
     * Direction degree of wind (0...359).
     */
    @NotNull
    @Min(0)
    @Max(359)
    private Integer direction;
}
