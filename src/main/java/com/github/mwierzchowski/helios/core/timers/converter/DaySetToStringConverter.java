package com.github.mwierzchowski.helios.core.timers.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.DayOfWeek;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Arrays.stream;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toCollection;

/**
 * Converts set of {@link DayOfWeek} objects into a string where each of days is represented by a number
 * (e.g. {@link DayOfWeek#MONDAY} is 1), separated by {@link DaySetToStringConverter#DAY_DELIMITER}.
 */
@Converter
public class DaySetToStringConverter implements AttributeConverter<Set<DayOfWeek>, String> {
    /**
     * Delimiter
     */
    private static final String DAY_DELIMITER = ",";

    /**
     * Converts set to string
     * @param days days set
     * @return string
     */
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

    /**
     * Converts string to days set
     * @param daysString string
     * @return set of days
     */
    @Override
    public Set<DayOfWeek> convertToEntityAttribute(String daysString) {
        if (daysString == null) {
            return null;
        } else if (daysString.trim().isEmpty()) {
            return emptySet();
        } else {
            return stream(daysString.split(DAY_DELIMITER))
                    .map(Integer::valueOf)
                    .sorted()
                    .map(DayOfWeek::of)
                    .collect(toCollection(LinkedHashSet::new));
        }
    }
}