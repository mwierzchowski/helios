package com.github.mwierzchowski.helios.service;

import com.github.mwierzchowski.helios.core.commons.EventStore;
import com.github.mwierzchowski.helios.core.commons.NotFoundException;
import com.github.mwierzchowski.helios.core.timers.Timer;
import com.github.mwierzchowski.helios.core.timers.TimerAlertPublisher;
import com.github.mwierzchowski.helios.core.timers.TimerRemovedEvent;
import com.github.mwierzchowski.helios.core.timers.TimerRepository;
import com.github.mwierzchowski.helios.core.timers.TimerSchedule;
import com.github.mwierzchowski.helios.service.constraint.TimerDescription;
import com.github.mwierzchowski.helios.service.dto.RequestErrorDto;
import com.github.mwierzchowski.helios.service.dto.ServiceErrorDto;
import com.github.mwierzchowski.helios.service.dto.TimerDto;
import com.github.mwierzchowski.helios.service.dto.TimerScheduleDto;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.List;

import static java.text.MessageFormat.format;
import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.mapstruct.ReportingPolicy.IGNORE;

/**
 * Management service for timers.
 * @author Marcin Wierzchowski
 */
@Slf4j
@Service
@Validated
@Transactional
@RequiredArgsConstructor
@OpenAPIDefinition(tags = {
        @Tag(name = "Timers", description = "Timers management"),
        @Tag(name = "Timer Schedules", description = "Timer schedules management")})
@ApiResponse(description = "Success")
@ApiResponse(description = "Bad request", responseCode = "4xx",
        content = @Content(schema = @Schema(implementation = RequestErrorDto.class)))
@ApiResponse(description = "Service failure", responseCode = "5xx",
        content = @Content(schema = @Schema(implementation = ServiceErrorDto.class)))
@Path("/v1/timers")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class TimerService {
    /**
     * Mapper
     */
    private static final TimerMapper MAPPER = Mappers.getMapper(TimerMapper.class);

    /**
     * Timer repository
     */
    private final TimerRepository timerRepository;

    /**
     * Timer alerts starter
     */
    private final TimerAlertPublisher alertPublisher;

    /**
     * Events publisher
     */
    private final EventStore eventStore;

    /**
     * Provides list of all registered timers.
     * @return list of timers
     */
    @GET
    @Tag(name = "Timers")
    @Operation(summary = "List of timers", description = "Provides list of defined timers")
    public List<TimerDto> getTimers() {
        log.debug("Searching for timers");
        return timerRepository.findAll().stream()
                .map(MAPPER::toTimerDto)
                .collect(toList());
    }

    /**
     * Registers new timer. It does nothing if timer with given description already exists.
     * @param timerDto timer to be registered
     */
    @POST
    @Tag(name = "Timers")
    @Operation(summary = "Add timer", description = "Adds new timer if it does not exist")
    public void addTimer(@NotNull @Valid @RequestBody(description = "Timer to be added") TimerDto timerDto) {
        log.debug("Adding timer with description '{}'", timerDto.getDescription());
        var foundTimer = timerRepository.findByDescription(timerDto.getDescription());
        if (foundTimer.isPresent()) {
            log.warn("Did not add timer with description '{}' as it exists (id '{}')",
                    timerDto.getDescription(), foundTimer.get().getId());
            return;
        }
        var timer = MAPPER.toTimer(timerDto);
        timerRepository.save(timer);
    }

    /**
     * De-registers timer. It does nothing, if timer is already de-registered.
     * @param timerId id of timer to be de-registered
     */
    @DELETE
    @Path("/{timerId}")
    @Tag(name = "Timers")
    @Operation(summary = "Remove timer", description = "Remove timer if it exists")
    public void removeTimer(
            @PathParam("timerId") @Parameter(description = "Timer id", example = "1") Integer timerId) {
        log.debug("Removing timer {}", timerId);
        var foundTimer = timerRepository.findById(timerId);
        if (foundTimer.isEmpty()) {
            log.warn("Did not remove timer {} as it does not exist", timerId);
            return;
        }
        var timer = foundTimer.get();
        timerRepository.delete(timer);
        var timerRemovedEvent = new TimerRemovedEvent(timer);
        eventStore.publish(timerRemovedEvent);
    }

    /**
     * Changes description of the timer. It does nothing, if timer already has given description.
     * @throws IllegalArgumentException when timer with given description already exists
     * @param timerId id of timer that should have new description
     * @param newDescription new description
     */
    @PUT
    @Path("/{timerId}")
    @Tag(name = "Timers")
    @Operation(summary = "Update timer's description", description = "Update timer's description if it was not updated")
    public void changeTimerDescription(
            @PathParam("timerId") @Parameter(description = "Timer id", example = "1") Integer timerId,
            @NotNull @TimerDescription @RequestBody(description = "Timer description", content = @Content(examples = {
                    @ExampleObject(summary = "Example timer", value = "New test timer")})) String newDescription) {
        log.debug("Changing timer {} description to '{}'", timerId, newDescription);
        var timer = timerRepository.findById(timerId).orElseThrow(() -> new NotFoundException(Timer.class, timerId));
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
    @GET
    @Path("/{timerId}/schedules")
    @Tag(name = "Timer Schedules")
    @Operation(summary = "List of timer's schedules", description = "Provides list of timer's schedules")
    public List<TimerScheduleDto> getSchedules(
            @PathParam("timerId") @Parameter(description = "Timer id", example = "1") Integer timerId) {
        log.debug("Searching for schedules of timer {}", timerId);
        return timerRepository.findById(timerId)
                .orElseThrow(() -> new NotFoundException(Timer.class, timerId))
                .getSchedules().stream()
                .map(MAPPER::toTimerScheduleDto)
                .collect(toList());
    }

    /**
     * Adds new schedule for the timer. It does nothing, if timer already has the same schedule. For given day of week
     * timer may have only 1 schedule.
     * @throws IllegalArgumentException when new schedule overlaps with existing one (e.g. they share a day in common).
     * @param timerId id of the timer
     * @param scheduleDto schedule
     */
    @POST
    @Path("/{timerId}/schedules")
    @Tag(name = "Timer Schedules")
    @Operation(summary = "Add schedule to timer", description = "Adds new timer's schedule if it does not exist")
    public void addSchedule(
            @PathParam("timerId") @Parameter(description = "Timer id", example = "1") Integer timerId,
            @NotNull @Valid @RequestBody(description = "Schedule to be added") TimerScheduleDto scheduleDto) {
        log.debug("Adding schedule to timer {}", timerId);
        var timer = timerRepository.findById(timerId).orElseThrow(() -> new NotFoundException(Timer.class, timerId));
        var schedule = MAPPER.toTimerSchedule(scheduleDto);
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
        alertPublisher.startAlertFor(timer);
    }

    /**
     * Removes schedule from timer. It does nothing, if schedule was already removed.
     * @param timerId id of timer
     * @param scheduleId id of schedule
     */
    @DELETE
    @Path("/{timerId}/schedules/{scheduleId}")
    @Tag(name = "Timer Schedules")
    @Operation(summary = "Remove schedule from timer", description = "Removes timer's schedule if it exists")
    public void removeSchedule(
            @PathParam("timerId") @Parameter(description = "Timer id", example = "1") Integer timerId,
            @PathParam("scheduleId") @Parameter(description = "Schedule id", example = "1") Integer scheduleId) {
        log.debug("Removing schedule {} from timer {}", scheduleId, timerId);
        var timer = timerRepository.findById(timerId).orElseThrow(() -> new NotFoundException(Timer.class, timerId));
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
     * Maps domain entities and DTOs.
     */
    @Mapper(unmappedTargetPolicy = IGNORE)
    interface TimerMapper {
        @Mapping(target = "id", ignore = true)
        Timer toTimer(TimerDto timerDto);

        @Mapping(target = "id", ignore = true)
        TimerSchedule toTimerSchedule(TimerScheduleDto scheduleDto);

        @Mapping(target = "scheduled", expression = "java(timer.getSchedules().size() > 0)")
        TimerDto toTimerDto(Timer timer);

        TimerScheduleDto toTimerScheduleDto(TimerSchedule schedule);
    }
}
