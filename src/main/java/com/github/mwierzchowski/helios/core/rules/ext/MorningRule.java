package com.github.mwierzchowski.helios.core.rules.ext;

import com.github.mwierzchowski.helios.core.rules.SceneRule;
import com.github.mwierzchowski.helios.core.sun.SunEphemerisType;
import com.github.mwierzchowski.helios.core.timers.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class MorningRule extends SceneRule {
    private static final String TIMER_FACT = Timer.class.getSimpleName();
    private static final String EPHEMERIS_FACT = SunEphemerisType.class.getSimpleName();

    public boolean check(Map<String, Object> facts) {
        return false;
    }
}
