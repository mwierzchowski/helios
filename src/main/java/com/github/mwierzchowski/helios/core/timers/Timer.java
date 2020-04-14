package com.github.mwierzchowski.helios.core.timers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Timer {
    @Id
    @GeneratedValue(generator = "timer_id_sequence")
    private Integer id;

    @NotNull
    private String description;

    @OneToMany
    private Set<TimerSchedule> schedules;

    @CreatedDate
    private Instant created;

    @LastModifiedDate
    private Instant updated;

    @Version
    private Integer version;

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
