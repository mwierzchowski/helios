package com.github.mwierzchowski.helios.core.commons;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

/**
 * Common properties
 * @author Marcin Wierzchowski
 */
@Data
@Component
@ConfigurationProperties("helios.common")
public class CommonProperties {
    /**
     * Flag saying if helios events should be published on application startup.
     */
    private boolean processingOnStartupEnabled = true;

    /**
     * Timestamp format
     */
    private String timeFormat = "YYYY-MM-dd HH:mm:ss.SSS";

    @Cacheable("commons-time-formatter")
    public DateTimeFormatter timeFormatter() {
        return DateTimeFormatter.ofPattern(timeFormat);
    }
}
