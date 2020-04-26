package com.github.mwierzchowski.helios.adapter.geoip;

import com.github.mwierzchowski.helios.core.commons.Location;
import com.github.mwierzchowski.helios.core.commons.LocationProvider;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.ZoneId;
import java.util.Objects;

/**
 * {@link LocationProvider} that provides location based on geo location of the machine's IP.
 * @author Marcin Wierzchowski
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnMissingBean(name = "locationProvider")
public class GeoipLocationProvider implements LocationProvider {
    /**
     * Properties
     */
    private final GeoipProperties geoipProperties;

    /**
     * IP based location (cached)
     */
    private Location location;

    /**
     * Initialize location and report.
     */
    @PostConstruct
    public void initialize() throws IOException, GeoIp2Exception {
        var dbStream = geoipProperties.getDatabase().getInputStream();
        var databaseReader = new DatabaseReader.Builder(dbStream).build();
        var databaseResponse = databaseReader.city(publicIpAddress());
        location = new Location(
                databaseResponse.getCity().getName(),
                databaseResponse.getLocation().getLatitude(),
                databaseResponse.getLocation().getLongitude()
        );
        log.info("IP based location is {} (lat={}, lon={})",
                location.getCity(), location.getLatitude(), location.getLongitude()
        );
        var systemTimeZone = ZoneId.systemDefault().getId();
        var ipTimeZone = databaseResponse.getLocation().getTimeZone();
        if (!Objects.equals(systemTimeZone, ipTimeZone)) {
            log.warn("System time zone ({}) does not match IP located time zone ({})", systemTimeZone, ipTimeZone);
        }
    }

    /**
     * Main provider method. Return cached location calculated on application start.
     * @return location
     */
    @Override
    public Location locate() {
        return location;
    }

    /**
     * Helper method that finds IP of the machine
     * @return ip
     * @throws UnknownHostException
     */
    private InetAddress publicIpAddress() throws UnknownHostException {
        var checkerResponse = new RestTemplate().getForObject(geoipProperties.getIpCheckerUrl(), String.class);
        if (checkerResponse == null || checkerResponse.isEmpty()) {
            throw new IllegalStateException("Could not determine public IP of this machine");
        }
        var ipAddress = InetAddress.getByName(checkerResponse.trim());
        log.debug("This machine has public IP {}", ipAddress.getHostAddress());
        return ipAddress;
    }
}
