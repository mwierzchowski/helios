package com.github.mwierzchowski.helios.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

/**
 * DTO for request errors.
 * @author Marcin Wierzchowski
 */
@Data
public class RequestErrorDto {
    /**
     * Error message
     */
    @NotNull
    @Schema(description = "Error message", example = "Timer with id 1 was not found", accessMode = READ_ONLY)
    private String message;

    /**
     * Optional object name
     */
    @Schema(description = "Object name that caused error", example = "Timer", accessMode = READ_ONLY)
    private String object;

    /**
     * Optional object value
     */
    @Schema(description = "Object value that caused error", example = "1", accessMode = READ_ONLY)
    private Object value;
}
