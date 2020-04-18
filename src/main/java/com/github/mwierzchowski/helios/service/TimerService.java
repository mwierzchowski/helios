package com.github.mwierzchowski.helios.service;

import com.github.mwierzchowski.helios.core.timers.TimerAlertStarter;
import com.github.mwierzchowski.helios.core.timers.TimerRemovedEvent;
import com.github.mwierzchowski.helios.core.timers.TimerRepository;
import com.github.mwierzchowski.helios.service.dto.TimerDto;
import com.github.mwierzchowski.helios.service.dto.TimerScheduleDto;
import com.github.mwierzchowski.helios.service.mapper.TimerServiceMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

import static java.text.MessageFormat.format;
import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Management service for timers.
 * @author Marcin WIerzchowski
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@Path("/v1/timers")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Tag(name = "Timers", description = "Service for timers management")
public class TimerService {
    /**
     * Entity to dto mapper
     */
    private final TimerServiceMapper serviceMapper;

    /**
     * Timer repository
     */
    private final TimerRepository timerRepository;

    /**
     * Timer alerts starter
     */
    private final TimerAlertStarter timerAlertStarter;

    /**
     * Events publisher
     */
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Provides list of all registered timers.
     * @return list of timers
     */
    @GET
    @Operation(summary = "List of all timers", description = "Provides list of all timers from repository")
    public List<TimerDto> getTimers() {
        log.debug("Searching for timers");
        return timerRepository.findAll().stream()
                .map(serviceMapper::toTimerDto)
                .collect(toList());
    }

    /**
     * Registers new timer. It does nothing if timer with given description already exists.
     * @param timerDto timer to be registered
     */
    public void addTimer(TimerDto timerDto) {
        log.debug("Adding timer with description '{}'", timerDto.getDescription());
        var foundTimer = timerRepository.findByDescription(timerDto.getDescription());
        if (foundTimer.isPresent()) {
            log.warn("Did not add timer with description '{}' as it exists (id '{}')",
                    timerDto.getDescription(), foundTimer.get().getId());
            return;
        }
        var timer = serviceMapper.toTimer(timerDto);
        timerRepository.save(timer);
    }

    /**
     * De-registers timer. It does nothing, if timer is already deregistered.
     * @param timerId id of timer to be deregistered
     */
    public void removeTimer(Integer timerId) {
        log.debug("Removing timer {}", timerId);
        var foundTimer = timerRepository.findById(timerId);
        if (foundTimer.isEmpty()) {
            log.warn("Did not remove timer {} as it does not exist", timerId);
            return;
        }
        var timer = foundTimer.get();
        timerRepository.delete(timer);
        var timerRemovedEvent = new TimerRemovedEvent(timer);
        eventPublisher.publishEvent(timerRemovedEvent);
    }

    /**
     * Changes description of the timer. It does nothing, if timer already has given description.
     * @throws IllegalArgumentException when timer with given description already exists
     * @param timerId id of timer that should have new description
     * @param newDescription new description
     */
    public void changeTimerDescription(Integer timerId, String newDescription) {
        log.debug("Changing timer {} description to '{}'", timerId, newDescription);
        var timer = timerRepository.findById(timerId).orElseThrow(notFound(timerId));
        if (timer.getDescription().equals(newDescription)) {
            log.warn("Did not change timer {} description to '{}' as this is current description",
                    timerId, newDescription);
            return;
        }
        var foundTimer = timerRepository.findByDescription(newDescription);
        if (foundTimer.isPresent()) {
            throw new IllegalArgumentException(
                    format("Could not change timer {0} description to '{1}' as it exists (id '{2}')",
                    timerId, newDescription, foundTimer.get().getId()));
        }
        timer.setDescription(newDescription);
        timerRepository.save(timer);
    }

    /**
     * Provides list of timer schedules.
     * @param timerId id of timer
     * @return list of schedules
     */
    public List<TimerScheduleDto> getSchedules(Integer timerId) {
        log.debug("Searching for schedules of timer {}", timerId);
        return timerRepository.findById(timerId)
                .orElseThrow(notFound(timerId))
                .getSchedules().stream()
                .map(serviceMapper::toTimerScheduleDto)
                .collect(toList());
    }

    /**
     * Adds new schedule for the timer. It does nothing, if timer already has the same schedule. For given day of week
     * timer may have only 1 schedule.
     * @throws IllegalArgumentException when new schedule overlaps with existing one (e.g. they share a day in common).
     * @param timerId id of the timer
     * @param scheduleDto schedule
     */
    public void addSchedule(Integer timerId, TimerScheduleDto scheduleDto) {
        log.debug("Adding schedule to timer {}", timerId);
        var timer = timerRepository.findById(timerId).orElseThrow(notFound(timerId));
        var schedule = serviceMapper.toTimerSchedule(scheduleDto);
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
        timerAlertStarter.startAlertFor(timer);
    }

    /**
     * Removes schedule from timer. It does nothing, if schedule was already removed.
     * @param timerId id of timer
     * @param scheduleId id of schedule
     */
    public void removeSchedule(Integer timerId, Integer scheduleId) {
        log.debug("Removing schedule {} from timer {}", scheduleId, timerId);
        var timer = timerRepository.findById(timerId).orElseThrow(notFound(timerId));
        var foundSchedule = timer.getSchedule(scheduleId);
        if (foundSchedule.isEmpty()) {
            log.warn("Did not remove schedule {} from timer {} as schedule does not exist",
                    scheduleId, timerId);
            return;
        }
        timer.getSchedules().remove(foundSchedule.get());
        timerRepository.save(timer);
    }

    /**
     * Helper method that builds correct exception for cases when timer or schedule does not exist.
     * @param id id of element that is missing
     * @return supplier with {@link NoSuchElementException}
     */
    private Supplier<RuntimeException> notFound(Integer id) {
        var message = "Not found timer with id {1}";
        return () -> new NoSuchElementException(format(message, id));
    }
}
