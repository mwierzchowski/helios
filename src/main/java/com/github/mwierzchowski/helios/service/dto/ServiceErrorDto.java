package com.github.mwierzchowski.helios.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

/**
 * DTO for service internal errors.
 * @author Marcin Wierzchowski
 */
@Data
public class ServiceErrorDto {
    /**
     * Error message
     */
    @NotNull
    @Schema(description = "Error message", example = "Unhandled exception", accessMode = READ_ONLY)
    private String message;

    /**
     * Exception
     */
    @Schema(description = "Exception that caused error", example = "java.lang.NullPointerException", accessMode = READ_ONLY)
    private String exception;

    /**
     * Timestamp
     */
    @Schema(description = "Timestamp of the exception", example = "2020-04-19 20:20:44.702", accessMode = READ_ONLY)
    private String timestamp;

    /**
     * Correlation id of failed execution
     */
    @Schema(description = "Correlation id of failed execution", example = "MISSING", accessMode = READ_ONLY)
    private String correlationId;
}
