package com.github.mwierzchowski.helios.core.locations;

import com.github.mwierzchowski.helios.HeliosProperties;
import com.github.mwierzchowski.helios.HeliosProperties.LocationProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Implementation {@link LocationProvider} that returns configured static location from application properties.
 * Location is loaded only on the application start and cached for all future calls.
 * @author Marcin Wierzchowski
 */
@Component
@Slf4j
@ConditionalOnProperty(prefix = "helios.location", name = {"latitude", "longitude"})
public class StaticLocationProvider implements LocationProvider {
    /**
     * Constant for cities that name is unknown (null).
     */
    public static String UNKNOWN_CITY = "unknown city";

    /**
     * Cached location build based on configuration.
     */
    private final Location staticLocation;

    /**
     * Main constructor.
     * @param properties application properties containing static location.
     */
    @Autowired
    public StaticLocationProvider(HeliosProperties properties) {
        LocationProperties locationProperties = properties.getLocation();
        String city = locationProperties.getCity() == null ? UNKNOWN_CITY : locationProperties.getCity();
        double latitude = locationProperties.getLatitude();
        double longitude = locationProperties.getLongitude();
        log.info("Static location is {} (lat={}, lon={})", city, latitude, longitude);
        staticLocation = new Location(city, latitude, latitude);
    }

    /**
     * Provides location.
     * @return location
     */
    @Override
    public Location locate() {
        return staticLocation;
    }
}
