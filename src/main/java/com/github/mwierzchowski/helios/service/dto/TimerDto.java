package com.github.mwierzchowski.helios.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimerDto {
    private Integer id;
    private String description;
    private Boolean scheduled;

    public static TimerDto of(String description) {
        return new TimerDto(null, description, null);
    }
}