package com.github.mwierzchowski.helios.service.dto;

import lombok.Data;

@Data
public class TimerDto {
    private Integer id;
    private String description;
    private Boolean scheduled;
}