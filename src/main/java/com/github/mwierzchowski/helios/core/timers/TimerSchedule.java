package com.github.mwierzchowski.helios.core.timers;

import com.github.mwierzchowski.helios.core.timers.converter.DaySetToStringConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Set;

import static java.time.LocalDate.now;
import static java.time.ZoneId.systemDefault;

@Data
@Entity
public class TimerSchedule {
    @Id
    @GeneratedValue(generator = "timerschedule_id_sequence")
    private Integer id;

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Timer timer;

    private LocalTime time;

    @Convert(converter = DaySetToStringConverter.class)
    private Set<DayOfWeek> days;

    @CreatedDate
    private Instant created;

    @LastModifiedDate
    private Instant updated;

    @Version
    private Integer version;

    public boolean isSame(TimerSchedule other) {
        return other != null
                && Objects.equals(this.time, other.time)
                && Objects.equals(this.days, other.days);
    }

    public boolean isOverlapping(TimerSchedule other) {
        return this.days.stream().anyMatch(other.days::contains);
    }

    public Instant nearestOccurrence() {
        LocalDate occurrenceDay = isToday() && isTimeNotOver() ? now() : nextDay();
        return occurrenceDay.atTime(time).atZone(systemDefault()).toInstant();
    }

    private boolean isToday() {
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        return days.contains(today);
    }

    private boolean isTimeNotOver() {
        return LocalTime.now().isBefore(time);
    }

    private LocalDate nextDay() {
        LocalDate thisDay = LocalDate.now();
        int today = thisDay.getDayOfWeek().getValue();
        int next = days.stream()
                .map(DayOfWeek::getValue)
                .filter(day -> day > today)
                .sorted()
                .findFirst()
                .orElse(7 + firstDayNextWeek());
        return thisDay.plusDays(next - today);
    }

    private Integer firstDayNextWeek() {
        return days.stream()
                .map(DayOfWeek::getValue)
                .sorted()
                .findFirst()
                .orElseThrow();
    }
}
