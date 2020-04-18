package com.github.mwierzchowski.helios.core.timers;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Info contributor for timers. Provides list of timers made of description and the timestamp of next alert.
 * @author Marcin Wierzchowski
 */
@Component
@RequiredArgsConstructor
public class TimerInfoContributor implements InfoContributor {
    /**
     * Timers repository
     */
    private final TimerRepository timerRepository;

    /**
     * Main contributions method
     * @param builder build
     */
    @Override
    public void contribute(Info.Builder builder) {
        List<Timer> timers = timerRepository.findAll();
        builder.withDetail("timers", timers.isEmpty() ? "none" : detailsOf(timers));
    }

    /**
     * Builds details of for all timers.
     * @param timers list of timers
     * @return details map
     */
    private Map<String, Object> detailsOf(List<Timer> timers) {
        var details = new HashMap<String, Object>();
        for (Timer timer : timers) {
            Object nearestOccurrence = timer.getNearestOccurrence().orElse(null);
            if (nearestOccurrence == null) {
                nearestOccurrence = "not scheduled";
            }
            details.put(timer.getDescription(), nearestOccurrence);
        }
        return details;
    }
}
