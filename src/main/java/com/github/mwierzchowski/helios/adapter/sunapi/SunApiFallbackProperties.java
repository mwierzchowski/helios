package com.github.mwierzchowski.helios.adapter.sunapi;

import com.github.mwierzchowski.helios.core.sun.SunEphemeris;
import lombok.Data;

import java.time.LocalDate;

import static com.github.mwierzchowski.helios.core.sun.SunEphemerisEventType.Dawn;
import static com.github.mwierzchowski.helios.core.sun.SunEphemerisEventType.Dusk;
import static com.github.mwierzchowski.helios.core.sun.SunEphemerisEventType.Noon;
import static com.github.mwierzchowski.helios.core.sun.SunEphemerisEventType.Sunrise;
import static com.github.mwierzchowski.helios.core.sun.SunEphemerisEventType.Sunset;
import static java.time.LocalTime.parse;

@Data
public class SunApiFallbackProperties {
    /**
     * Fallback dawn time.
     */
    private String dawn = "05:00:00";

    /**
     * Fallback sunrise time.
     */
    private String sunrise = "06:00:00";

    /**
     * Fallback noon time.
     */
    private String noon = "12:00:00";

    /**
     * Fallback sunset time.
     */
    private String sunset = "21:00:00";

    /**
     * Fallback dusk time.
     */
    private String dusk = "22:00:00";

    public SunEphemeris getSunEphemeris() {
        var ephemeris = new SunEphemeris();
        ephemeris.setDay(LocalDate.now());
        ephemeris.getTimes().put(Dawn, parse(dawn));
        ephemeris.getTimes().put(Sunrise, parse(sunrise));
        ephemeris.getTimes().put(Noon, parse(noon));
        ephemeris.getTimes().put(Sunset, parse(sunset));
        ephemeris.getTimes().put(Dusk, parse(dusk));
        ephemeris.setApproximated(true);
        return ephemeris;
    }
}
