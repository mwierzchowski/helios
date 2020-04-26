package com.github.mwierzchowski.helios.core.sun;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

/**
 * Sun ephemeris info contributor. Builds list of today events
 * @author Marcin Wierzchowski
 */
@Component
@RequiredArgsConstructor
public class SunEphemerisInfoContributor implements InfoContributor {
    /**
     * Ephemeris provider
     */
    private final SunEphemerisProvider ephemerisProvider;

    /**
     * Main contributor method
     * @param builder builder
     */
    @Override
    public void contribute(Info.Builder builder) {
        SunEphemeris ephemeris = ephemerisProvider.sunEphemerisFor(LocalDate.now());
        builder.withDetail("sunEphemeris", detailsOf(ephemeris));
    }

    /**
     * Helper method
     * @param ephemeris ephemeris
     * @return info details
     */
    private Map<String, Object> detailsOf(SunEphemeris ephemeris) {
        var details = new TreeMap<String, Object>();
        details.put("day", ephemeris.getDay());
        details.put("times", ephemeris.getTimes());
        details.put("approximated", ephemeris.getApproximated());
        return details;
    }
}
