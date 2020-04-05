package com.github.mwierzchowski.helios.core.timers;

import com.github.mwierzchowski.helios.core.HeliosEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TimerRemovedEvent extends HeliosEvent {
    private final Timer timer;
}
