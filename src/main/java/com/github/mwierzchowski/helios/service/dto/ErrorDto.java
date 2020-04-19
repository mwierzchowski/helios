package com.github.mwierzchowski.helios.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

/**
 * DTO for errors.
 * @author Marcin Wierzchowski
 */
@Data
public class ErrorDto {
    /**
     * Error message
     */
    @NotNull
    @Schema(description = "Error message", accessMode = READ_ONLY)
    private String message;

    /**
     * Optional property name
     */
    @Schema(description = "Optional property name that caused error", accessMode = READ_ONLY)
    private String property;
}
