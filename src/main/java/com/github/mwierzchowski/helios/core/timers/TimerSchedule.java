package com.github.mwierzchowski.helios.core.timers;

import com.github.mwierzchowski.helios.core.timers.converter.DaySetToStringConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import static java.time.LocalDate.now;
import static java.time.ZoneId.systemDefault;
import static lombok.AccessLevel.NONE;

/**
 * Entity representing timer (see {@link Timer}) schedule defined as week days and time when timer alert should occur.
 * @author Marcin Wierzchowski
 */
@Data
@Entity
@SequenceGenerator(name = "timer_schedule_id_generator", sequenceName = "timer_schedule_id_seq", allocationSize = 10)
public class TimerSchedule {
    /**
     * Id of the entity
     */
    @Id
    @GeneratedValue(generator = "timer_schedule_id_generator")
    private Integer id;

    /**
     * Timer for which this schedule belongs
     */
    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Timer timer;

    /**
     * Time when alert should occur
     */
    private LocalTime time;

    /**
     * Days when alert should occur
     */
    @Setter(NONE)
    @Convert(converter = DaySetToStringConverter.class)
    private Set<DayOfWeek> days = new TreeSet<>();

    /**
     * Timestamp of the creation for auditing purposes.
     */
    @CreatedDate
    private Instant created;

    /**
     * Timestamp of the update for auditing purposes.
     */
    @LastModifiedDate
    private Instant updated;

    /**
     * Version of the entity for auditing and optimistic locking purposes.
     */
    @Version
    private Integer version;

    /**
     * Returns true if schedule is same (in terms of business data) as given one. Only time and days are taken into
     * account.
     * @param other schedule to check
     * @return true if both schedules are the same or false when they are different or other is null
     */
    public boolean isSame(TimerSchedule other) {
        return other != null
                && Objects.equals(this.time, other.time)
                && Objects.equals(this.days, other.days);
    }

    /**
     * Returns true if schedule overlaps with given one. Overlaps means that they have at least 1 day in common.
     * @param other other schedule to check
     * @return result
     */
    public boolean isOverlapping(TimerSchedule other) {
        return this.days.stream().anyMatch(other.days::contains);
    }

    /**
     * Provides timestamp of the nearest timer alert.
     * @return timestamp
     */
    public Instant nearestOccurrence() {
        var occurrenceDay = isToday() && isTimeNotOver() ? now() : nextDay();
        return occurrenceDay.atTime(time).atZone(systemDefault()).toInstant();
    }

    /**
     * Returns true if schedule is for today, but it does not take into account current time of the day (e.g. if
     * schedule time today already passed). That check is made by {@link TimerSchedule#isTimeNotOver()}.
     * @return check result
     */
    private boolean isToday() {
        var thisDayOfWeek = LocalDate.now().getDayOfWeek();
        return days.contains(thisDayOfWeek);
    }

    /**
     * Returns true if time of the schedule already passed today, but it does not take into account if schedule was
     * planned today. That check is made by {@link TimerSchedule#isToday()}}.
     * @return check result
     */
    private boolean isTimeNotOver() {
        return LocalTime.now().isBefore(time);
    }

    /**
     * Finds the next day, after today, when schedule should fire.
     * @return next day
     */
    private LocalDate nextDay() {
        var thisDay = LocalDate.now();
        var thisDayOfWeek = (long) thisDay.getDayOfWeek().getValue();
        var nextDayOfWeek = (long) days.stream()
                .map(DayOfWeek::getValue)
                .filter(day -> day > thisDayOfWeek)
                .sorted()
                .findFirst()
                .orElse(7 + firstDayNextWeek());
        return thisDay.plusDays(nextDayOfWeek - thisDayOfWeek);
    }

    /**
     * Finds the earliest day a week when schedule should fire in general (does not take into account current day of
     * week).
     * @return earliest day next week
     */
    private Integer firstDayNextWeek() {
        return days.stream()
                .map(DayOfWeek::getValue)
                .sorted()
                .findFirst()
                .orElseThrow();
    }
}
