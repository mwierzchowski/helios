package com.github.mwierzchowski.helios.adapter.jpa;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.DayOfWeek;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toCollection;

@Converter(autoApply = true)
public class DaysOfWeekConverter implements AttributeConverter<Set<DayOfWeek>, String> {
    private static final String DAY_DELIMITER = ",";

    @Override
    public String convertToDatabaseColumn(Set<DayOfWeek> days) {
        if (days == null) {
            return null;
        } else {
            return days.stream()
                    .map(DayOfWeek::getValue)
                    .sorted()
                    .map(String::valueOf)
                    .collect(joining(DAY_DELIMITER));
        }
    }

    @Override
    public Set<DayOfWeek> convertToEntityAttribute(String daysString) {
        if (daysString == null) {
            return null;
        } else if (daysString.trim().isEmpty()) {
            return Collections.emptySet();
        } else {
            return stream(daysString.split(DAY_DELIMITER))
                    .map(Integer::valueOf)
                    .sorted()
                    .map(DayOfWeek::of)
                    .collect(toCollection(LinkedHashSet::new));
        }
    }
}