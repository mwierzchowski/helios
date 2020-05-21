package com.github.mwierzchowski.helios.adapter.sunapi;

import com.github.mwierzchowski.helios.core.sun.SunEphemeris;
import lombok.Data;

import java.time.LocalDate;

import static com.github.mwierzchowski.helios.core.sun.SunEphemerisType.DAWN;
import static com.github.mwierzchowski.helios.core.sun.SunEphemerisType.DUSK;
import static com.github.mwierzchowski.helios.core.sun.SunEphemerisType.NOON;
import static com.github.mwierzchowski.helios.core.sun.SunEphemerisType.SUNRISE;
import static com.github.mwierzchowski.helios.core.sun.SunEphemerisType.SUNSET;
import static java.time.LocalTime.parse;

/**
 * Properties for Sun API fallback
 */
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

    /**
     * Creates {@link SunEphemeris} from configured fallback
     * @return sun ephemeris
     */
    public SunEphemeris getSunEphemeris() {
        var ephemeris = new SunEphemeris();
        ephemeris.setDay(LocalDate.now());
        ephemeris.getTimes().put(DAWN, parse(dawn));
        ephemeris.getTimes().put(SUNRISE, parse(sunrise));
        ephemeris.getTimes().put(NOON, parse(noon));
        ephemeris.getTimes().put(SUNSET, parse(sunset));
        ephemeris.getTimes().put(DUSK, parse(dusk));
        ephemeris.setApproximated(true);
        return ephemeris;
    }
}
