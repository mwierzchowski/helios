package com.github.mwierzchowski.helios.core.locations;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Info contributor for location. It uses location provided by available {@link LocationProvider}.
 * @author Marcin Wierzchowski
 */
@Component
@RequiredArgsConstructor
public class LocationInfoContributor implements InfoContributor {
    /**
     * Location provider available in the application context.
     */
    private final LocationProvider locationProvider;

    /**
     * Main contributor method.
     * @param builder info builder
     */
    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("location", getDetails()).build();
    }

    /**
     * Helper method that builds location info.
     * @return map with location info
     */
    private Map<String, Object> getDetails() {
        Location location = locationProvider.locate();
        Map<String, Object> details = new LinkedHashMap<>();
        details.put("city", location.getCity());
        details.put("latitude", location.getLatitude());
        details.put("longitude", location.getLongitude());
        details.put("provider", locationProvider.getClass());
        return details;
    }
}
