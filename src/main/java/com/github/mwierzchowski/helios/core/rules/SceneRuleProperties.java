package com.github.mwierzchowski.helios.core.rules;

import com.github.mwierzchowski.helios.core.commons.HeliosEvent;
import com.github.mwierzchowski.helios.core.sun.SunEphemerisEvent;
import com.github.mwierzchowski.helios.core.timers.TimerAlertEvent;
import com.github.mwierzchowski.helios.core.weather.WeatherObservationEvent;
import com.github.mwierzchowski.helios.core.weather.WeatherStaleEvent;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@ConfigurationProperties("helios.rules")
public class SceneRuleProperties {
    private List<Class<? extends HeliosEvent<?>>> factEvents = new ArrayList<>();
    private List<Class<? extends HeliosEvent<?>>> resetEvents = new ArrayList<>();

    public SceneRuleProperties() {
        factEvents.add(WeatherObservationEvent.class);
        factEvents.add(SunEphemerisEvent.class);
        factEvents.add(TimerAlertEvent.class);
        resetEvents.add(WeatherStaleEvent.class);
    }
}
