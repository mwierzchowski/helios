package com.github.mwierzchowski.helios.service;

import com.github.mwierzchowski.helios.core.timers.Timer;
import com.github.mwierzchowski.helios.core.timers.TimerRemovedEvent;
import com.github.mwierzchowski.helios.core.timers.TimerRepository;
import com.github.mwierzchowski.helios.service.dto.TimerDto;
import com.github.mwierzchowski.helios.service.mappers.TimerServiceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class TimerService {
    private final TimerServiceMapper mapper;
    private final TimerRepository timerRepository;
    private final ApplicationEventPublisher eventPublisher;

    public List<TimerDto> getTimers() {
        log.debug("Searching for all timers");
        List<Timer> types = timerRepository.findAll();
        return mapper.toDtoList(types);
    }

    public void addTimer(TimerDto dto) {
        log.debug("Adding new timer '{}'", dto.getDescription());
        Optional<Timer> existingTimer = timerRepository.findByDescription(dto.getDescription());
        if (!existingTimer.isPresent()) {
            Timer timer = mapper.toTimer(dto);
            timerRepository.add(timer);
        }
    }

    public void removeTimer(Integer timerId) {
        log.debug("Removing timer id {}", timerId);
        timerRepository.findById(timerId).ifPresent(timer -> {
            timerRepository.remove(timer);
            eventPublisher.publishEvent(new TimerRemovedEvent(timer));
        });
    }
}
