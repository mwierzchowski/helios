package com.github.mwierzchowski.helios.service;

import com.github.mwierzchowski.helios.core.HeliosEvent;
import com.github.mwierzchowski.helios.core.timers.Timer;
import com.github.mwierzchowski.helios.core.timers.TimerRemovedEvent;
import com.github.mwierzchowski.helios.core.timers.TimerRepository;
import com.github.mwierzchowski.helios.core.timers.TimerSchedule;
import com.github.mwierzchowski.helios.service.dto.TimerDto;
import com.github.mwierzchowski.helios.service.dto.TimerScheduleDto;
import com.github.mwierzchowski.helios.service.mapper.TimerServiceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Supplier;

import static java.text.MessageFormat.format;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TimerService {
    private final TimerServiceMapper serviceMapper;
    private final TimerRepository timerRepository;
    private final ApplicationEventPublisher eventPublisher;

    public List<TimerDto> getTimers() {
        log.debug("Searching for timers");
        return timerRepository.findAll().stream()
                .map(serviceMapper::toTimerDto)
                .collect(toList());
    }

    public void addTimer(TimerDto timerDto) {
        log.debug("Adding timer with description '{}'", timerDto.getDescription());
        Optional<Timer> foundTimer = timerRepository.findByDescription(timerDto.getDescription());
        if (foundTimer.isPresent()) {
            log.warn("Did not add timer with description '{}' as it exists (id '{}')",
                    timerDto.getDescription(), foundTimer.get().getId());
            return;
        }
        Timer timer = serviceMapper.toTimer(timerDto);
        timerRepository.save(timer);
    }

    public void removeTimer(Integer timerId) {
        log.debug("Removing timer {}", timerId);
        Optional<Timer> foundTimer = timerRepository.findById(timerId);
        if (foundTimer.isEmpty()) {
            log.warn("Did not remove timer {} as it does not exist", timerId);
            return;
        }
        Timer timer = foundTimer.get();
        timerRepository.delete(timer);
        HeliosEvent timerRemovedEvent = new TimerRemovedEvent(timer);
        eventPublisher.publishEvent(timerRemovedEvent);
    }

    public void changeTimerDescription(Integer timerId, String newDescription) {
        log.debug("Changing timer {} description to '{}'", timerId, newDescription);
        Timer timer = timerRepository.findById(timerId).orElseThrow(notFound("timer", timerId));
        if (timer.getDescription().equals(newDescription)) {
            log.warn("Did not change timer {} description to '{}' as this is current description",
                    timerId, newDescription);
            return;
        }
        Optional<Timer> foundTimer = timerRepository.findByDescription(newDescription);
        if (foundTimer.isPresent()) {
            throw new IllegalArgumentException(
                    format("Could not change timer {0} description to '{1}' as it exists (id '{2}')",
                    timerId, newDescription, foundTimer.get().getId()));
        }
        timer.setDescription(newDescription);
        timerRepository.save(timer);
    }

    public List<TimerScheduleDto> getSchedules(Integer timerId) {
        log.debug("Searching for schedules of timer {}", timerId);
        return timerRepository.findById(timerId)
                .orElseThrow(notFound("timer", timerId))
                .getSchedules().stream()
                .map(serviceMapper::toTimerScheduleDto)
                .collect(toList());
    }

    public void addSchedule(Integer timerId, TimerScheduleDto scheduleDto) {
        log.debug("Adding schedule to timer {}", timerId);
        Timer timer = timerRepository.findById(timerId).orElseThrow(notFound("timer", timerId));
        TimerSchedule schedule = serviceMapper.toTimerSchedule(scheduleDto);
        if (timer.hasSame(schedule)) {
            log.warn("Did not add schedule to timer {} as it exist", timerId);
            return;
        }
        if (timer.hasOverlapping(schedule)) {
            throw new IllegalArgumentException(
                    format("Could not add schedule for timer {0} as some days are overlapping: {1}",
                    timerId, schedule.getDays()));
        }
        timer.add(schedule);
        timerRepository.save(timer);
    }

    public void removeSchedule(Integer timerId, Integer scheduleId) {
        log.debug("Removing schedule {} from timer {}", scheduleId, timerId);
        Timer timer = timerRepository.findById(timerId).orElseThrow(notFound("timer", timerId));
        Optional<TimerSchedule> foundSchedule = timer.getSchedule(scheduleId);
        if (foundSchedule.isEmpty()) {
            log.warn("Did not remove schedule {} from timer {} as schedule does not exist",
                    scheduleId, timerId);
            return;
        }
        timer.getSchedules().remove(foundSchedule.get());
        timerRepository.save(timer);
    }

    public void enableSchedule(Integer timerId, Integer scheduleId, Boolean enableFlag) {
        log.debug("Changing enable flag to '{}' for schedule {} of timer {}", enableFlag, scheduleId, timerId);
        Timer timer = timerRepository.findById(timerId).orElseThrow(notFound("timer", timerId));
        TimerSchedule schedule = timer.getSchedule(scheduleId).orElseThrow(notFound("schedule", scheduleId));
        if (schedule.getEnabled() == enableFlag) {
            log.warn("Did not change enable flag for schedule {} of timer {} as flag is already '{}'",
                    scheduleId, timerId, enableFlag);
            return;
        }
        schedule.setEnabled(enableFlag);
        timerRepository.save(timer);

    }

    private Supplier<RuntimeException> notFound(String element, Integer id) {
        String message = "Not found {0} with id {1}";
        return () -> new NoSuchElementException(format(message, element, id));
    }
}
