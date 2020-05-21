package com.github.mwierzchowski.helios.core.timers;

import com.github.mwierzchowski.helios.core.commons.HeliosEvent;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Event published at the time of timer alert.
 * @author Marcin Wierzchowski
 */
@Data
@RequiredArgsConstructor
public class TimerAlertEvent implements HeliosEvent<Void> {
    /**
     * Timer that triggered this alert.
     */
    private final Timer timer;
}
