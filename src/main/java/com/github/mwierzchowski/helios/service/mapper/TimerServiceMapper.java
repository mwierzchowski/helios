package com.github.mwierzchowski.helios.service.mapper;

import com.github.mwierzchowski.helios.core.timers.Timer;
import com.github.mwierzchowski.helios.core.timers.TimerSchedule;
import com.github.mwierzchowski.helios.service.dto.TimerDto;
import com.github.mwierzchowski.helios.service.dto.TimerScheduleDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE)
public interface TimerServiceMapper {
    @Mapping(target = "id", ignore = true)
    Timer toTimer(TimerDto timerDto);

    @Mapping(target = "id", ignore = true)
    TimerSchedule toTimerSchedule(TimerScheduleDto scheduleDto);

    @Mapping(target = "scheduled", expression = "java(timer.getSchedules().size() > 0)")
    TimerDto toTimerDto(Timer timer);

    TimerScheduleDto toTimerScheduleDto(TimerSchedule schedule);
}
