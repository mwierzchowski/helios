package com.github.mwierzchowski.helios.core.sun;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

@Component
@RequiredArgsConstructor
public class SunEphemerisInfoContributor implements InfoContributor {
    private final SunEphemerisProvider ephemerisProvider;

    @Override
    public void contribute(Info.Builder builder) {
        SunEphemeris ephemeris = ephemerisProvider.sunEphemerisFor(LocalDate.now());
        builder.withDetail("sunEphemeris", detailsOf(ephemeris));
    }

    private Map<String, Object> detailsOf(SunEphemeris ephemeris) {
        var details = new TreeMap<String, Object>();
        details.put("day", ephemeris.getDay());
        details.put("times", ephemeris.getTimes());
        details.put("approximated", ephemeris.getApproximated());
        return details;
    }
}
