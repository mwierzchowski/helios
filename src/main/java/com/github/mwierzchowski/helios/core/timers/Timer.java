package com.github.mwierzchowski.helios.core.timers;

import lombok.Data;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import static javax.persistence.CascadeType.ALL;
import static lombok.AccessLevel.NONE;

@Data
@Entity
public class Timer {
    @Id
    @GeneratedValue(generator = "timer_id_sequence")
    private Integer id;

    @NotNull
    private String description;

    @Setter(NONE)
    @OneToMany(mappedBy = "timer", cascade = ALL)
    private Set<TimerSchedule> schedules = new LinkedHashSet<>();

    @CreatedDate
    private Instant created;

    @LastModifiedDate
    private Instant updated;

    @Version
    private Integer version;

    public void add(TimerSchedule schedule) {
        schedule.setTimer(this);
        schedules.add(schedule);
    }

    public boolean hasSame(TimerSchedule schedule) {
        return schedules.stream().anyMatch(schedule::isSame);
    }

    public boolean hasOverlapping(TimerSchedule schedule) {
        return schedules.stream().anyMatch(schedule::isOverlapping);
    }

    public Optional<TimerSchedule> getSchedule(Integer scheduleId) {
        return schedules.stream()
                .filter(schedule -> schedule.getId().equals(scheduleId))
                .findFirst();
    }
}
