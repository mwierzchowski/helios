package com.github.mwierzchowski.helios.core.timers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Timer {
    @Id
    @GeneratedValue(generator = "timer_id_sequence")
    private Integer id;

    private String description;
}
