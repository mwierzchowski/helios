package com.github.mwierzchowski.helios.core.timers;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TimerInfoContributor implements InfoContributor {
    private final TimerRepository timerRepository;

    @Override
    public void contribute(Info.Builder builder) {
        List<Timer> timers = timerRepository.findAll();
        builder.withDetail("timers", timers.isEmpty() ? "none" : detailsOf(timers));
    }

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
