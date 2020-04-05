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
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import java.time.Instant;

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

    @CreatedDate
    private Instant created;

    @LastModifiedDate
    private Instant updated;

    @Version
    private Integer version;
}
