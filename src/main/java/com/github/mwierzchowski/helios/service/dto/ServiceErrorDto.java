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
    @Schema(description = "Error message", accessMode = READ_ONLY)
    private String message;

    /**
     * Exception
     */
    @Schema(description = "Exception that caused error", accessMode = READ_ONLY)
    private String exception;

    /**
     * Timestamp
     */
    @Schema(description = "Timestamp of the exception", accessMode = READ_ONLY)
    private String timestamp;

    /**
     * Correlation id of failed execution
     */
    @Schema(description = "Correlation id of failed execution", accessMode = READ_ONLY)
    private String correlationId;
}
