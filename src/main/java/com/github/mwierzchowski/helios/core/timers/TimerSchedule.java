package com.github.mwierzchowski.helios.core.timers;

import com.github.mwierzchowski.helios.adapter.jpa.DaysOfWeekConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
import java.time.LocalTime;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimerSchedule {
    @Id
    @GeneratedValue(generator = "timerschedule_id_sequence")
    private Integer id;

    @ManyToOne
    private Timer timer;

    private LocalTime time;

    @Convert(converter = DaysOfWeekConverter.class)
    private Set<DayOfWeek> days;

    private Boolean enabled;

    @CreatedDate
    private Instant created;

    @LastModifiedDate
    private Instant updated;

    @Version
    private Integer version;

    public boolean isConflictedWith(TimerSchedule other) {
        return this.days.stream().anyMatch(other.days::contains);
    }

    public boolean isSameAs(TimerSchedule other) {
        return other != null
                && Objects.equals(this.time, other.time)
                && Objects.equals(this.days, other.days);
    }
}
