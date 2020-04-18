package com.github.mwierzchowski.helios.service.mapper;

import com.github.mwierzchowski.helios.core.timers.Timer;
import com.github.mwierzchowski.helios.core.timers.TimerSchedule;
import com.github.mwierzchowski.helios.service.dto.TimerDto;
import com.github.mwierzchowski.helios.service.dto.TimerScheduleDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.ReportingPolicy.IGNORE;

/**
 * Mapper that translates domain entities and DTOs for {@link com.github.mwierzchowski.helios.service.TimerService}.
 * @author Marcin Wierzchowski
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE)
public interface TimerServiceMapper {
    /**
     * Translates {@link TimerDto} to {@link Timer}. As client can not assign IDs, that property is ignored.
     * @param timerDto dto
     * @return entity
     */
    @Mapping(target = "id", ignore = true)
    Timer toTimer(TimerDto timerDto);

    /**
     * Translates {@link TimerScheduleDto} to {@link TimerSchedule}. As client can not assign IDs, that property is
     * ignored.
     * @param scheduleDto dto
     * @return entity
     */
    @Mapping(target = "id", ignore = true)
    TimerSchedule toTimerSchedule(TimerScheduleDto scheduleDto);

    /**
     * Translates {@link Timer} to {@link TimerDto}.
     * @param timer entity
     * @return dto
     */
    @Mapping(target = "scheduled", expression = "java(timer.getSchedules().size() > 0)")
    TimerDto toTimerDto(Timer timer);

    /**
     *Translates {@link TimerSchedule} to {@link TimerScheduleDto}.
     * @param schedule entity
     * @return dto
     */
    TimerScheduleDto toTimerScheduleDto(TimerSchedule schedule);
}
