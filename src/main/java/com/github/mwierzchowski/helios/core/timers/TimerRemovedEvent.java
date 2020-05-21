package com.github.mwierzchowski.helios.core.timers;

import com.github.mwierzchowski.helios.core.commons.HeliosEvent;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Event published at the time of timer removal.
 * @author Marcin Wierzchowski
 */
@Data
@RequiredArgsConstructor
public class TimerRemovedEvent implements HeliosEvent<Void> {
    /**
     * Timer that was removed
     */
    private final Timer timer;
}
