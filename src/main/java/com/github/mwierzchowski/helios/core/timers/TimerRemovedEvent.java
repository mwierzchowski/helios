package com.github.mwierzchowski.helios.core.timers;

import com.github.mwierzchowski.helios.core.HeliosEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * Event published at the time of timer removal.
 * @author Marcin Wierzchowski
 */
@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TimerRemovedEvent extends HeliosEvent {
    /**
     * Timer that was removed
     */
    private final Timer timer;
}
