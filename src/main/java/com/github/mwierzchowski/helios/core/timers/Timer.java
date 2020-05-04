package com.github.mwierzchowski.helios.core.timers;

import lombok.Data;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;
import static lombok.AccessLevel.NONE;

/**
 * Entity representing timers. Each timer needs to have unique description. It may also have zero or more
 * schedules (see {@link TimerSchedule}).
 * @author Marcin Wierzchowski
 */
@Data
@Entity
@SequenceGenerator(name = "timer_id_generator", sequenceName = "timer_id_seq", allocationSize = 10)
public class Timer {
    /**
     * Id of the entity
     */
    @Id
    @GeneratedValue(generator = "timer_id_generator")
    private Integer id;

    /**
     * Timer description
     */
    @NotNull
    private String description;

    /**
     * Schedules when timer gives alert.
     */
    @Setter(NONE)
    @OneToMany(mappedBy = "timer", fetch = EAGER, cascade = ALL, orphanRemoval = true)
    private Set<TimerSchedule> schedules = new LinkedHashSet<>();

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
     * Add schedule to the timer. As a result, new schedule is linked with the timer.
     * @param schedule schedule
     */
    public void add(TimerSchedule schedule) {
        schedule.setTimer(this);
        schedules.add(schedule);
    }

    /**
     * Returns true if timer already has a schedule that is same (in terms of business data) as given schedule. See
     * {@link TimerSchedule#isSame(TimerSchedule)} for details.
     * @param schedule schedule
     * @return result
     */
    public boolean hasSame(TimerSchedule schedule) {
        return schedules.stream().anyMatch(schedule::isSame);
    }

    /**
     * Returns true if timer already has at least 1 schedule that overlaps with given schedule. See
     * {@link TimerSchedule#isOverlapping(TimerSchedule)} for details.
     * @param schedule schedule
     * @return result
     */
    public boolean hasOverlapping(TimerSchedule schedule) {
        return schedules.stream().anyMatch(schedule::isOverlapping);
    }

    /**
     * Provides optional with schedule of given id.
     * @param scheduleId schedule id
     * @return optional schedule or empty if schedule is missing
     */
    public Optional<TimerSchedule> getSchedule(Integer scheduleId) {
        return schedules.stream()
                .filter(schedule -> schedule.getId().equals(scheduleId))
                .findFirst();
    }

    /**
     * Provides optional with timestamp of the nearest timer alert.
     * @return optional with timestamp or empty if timer has no schedules
     */
    public Optional<Instant> getNearestOccurrence() {
        return schedules.stream()
                .map(TimerSchedule::nearestOccurrence)
                .sorted()
                .findFirst();
    }

    public void dupa(Integer z) {
        int y = 754;
        String x = null;
        System.out.println(x.indexOf('c'));
        System.out.println(y);
    }
}
