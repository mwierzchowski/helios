package com.github.mwierzchowski.helios.core.commons;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("helios.common")
public class CommonProperties {
    /**
     * Flag saying if helios events should be published on application startup.
     */
    private boolean publishEventsOnStartup = true;
}
