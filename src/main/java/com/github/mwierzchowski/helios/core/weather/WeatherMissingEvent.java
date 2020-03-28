package com.github.mwierzchowski.helios.core.weather;

import com.github.mwierzchowski.helios.core.HeliosEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WeatherMissingEvent extends HeliosEvent {
}
