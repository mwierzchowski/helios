package com.github.mwierzchowski.helios.core.timers;

import com.github.mwierzchowski.helios.core.HeliosEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * Event published at the time of timer alert.
 * @author Marcin Wierzchowski
 */
@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TimerAlertEvent extends HeliosEvent {
    /**
     * Timer that triggered this alert.
     */
    private final Timer timer;
}
