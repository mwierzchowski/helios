package com.github.mwierzchowski.helios.adapter.owm;

import com.github.mwierzchowski.helios.core.weather.Weather;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.openweathermap.model.CurrentWeatherResponse;

import static org.mapstruct.ReportingPolicy.IGNORE;

/**
 * OWM domain mapper.
 * @author Marcin Wierzchowski
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE)
public interface OwmDomainMapper {
    /**
     * Map {@link CurrentWeatherResponse} into {@link Weather}.
     * @param response from API
     * @return domain object
     */
    @Mapping(target = "timestamp", expression = "java(java.time.Instant.ofEpochSecond(response.getDt()))") // TODO
    @Mapping(target = "temperature.value", source = "response.main.temp")
    @Mapping(target = "temperature.unit", constant = "Celsius") // TODO
    @Mapping(target = "wind.speed.value", source ="response.wind.speed")
    @Mapping(target = "wind.speed.unit", constant = "MetersPerSecond") // TODO
    @Mapping(target = "wind.direction", source ="response.wind.deg")
    @Mapping(target = "cloudsCoverage", source ="response.clouds.all")
    Weather toWeather(CurrentWeatherResponse response);
}

