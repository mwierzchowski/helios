package com.github.mwierzchowski.helios.adapter.geoip;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * GeoIP properties.
 * @author Marcin Wierzchowski
 */
@Data
@Slf4j
@Component
@ConfigurationProperties("helios.geoip")
public class GeoipProperties {
    /**
     * URL of the service that checks actual public IP of the local machine. Service is expected to return IP as
     * plain text.
     */
    private String ipCheckerUrl = "http://checkip.amazonaws.com";

    /**
     * Path to the GeoLite IP database. Could be classpath or file on the filesystem.
     */
    private Resource database = new ClassPathResource("GeoLite2-City.mmdb");
}
