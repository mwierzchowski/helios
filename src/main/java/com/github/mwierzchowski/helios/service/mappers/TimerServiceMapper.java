package com.github.mwierzchowski.helios.service.mappers;

import com.github.mwierzchowski.helios.core.timers.Timer;
import com.github.mwierzchowski.helios.service.dto.TimerDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE)
public interface TimerServiceMapper {
    @Mapping(target = "id", ignore = true)
    Timer toTimer(TimerDto dto);

    TimerDto toDto(Timer timerType);

    List<TimerDto> toDtoList(List<Timer> timerTypes);
}
