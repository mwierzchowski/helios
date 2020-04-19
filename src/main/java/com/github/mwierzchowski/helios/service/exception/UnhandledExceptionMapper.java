package com.github.mwierzchowski.helios.service.exception;

import com.github.mwierzchowski.helios.service.dto.ServiceErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.time.Instant;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

/**
 * Exception mapper for translating general {@link Exception} to {@link ServiceErrorDto}.
 * @author Marcin Wierzchowski
 */
@Slf4j
@Provider
@Component
public class UnhandledExceptionMapper implements ExceptionMapper<Exception> {
    /**
     * Main mapper method
     * @param exception exception
     * @return response
     */
    @Override
    public Response toResponse(Exception exception) {
        log.error("Unhandled exception", exception);
        var errorDto = new ServiceErrorDto();
        errorDto.setMessage(exception.getMessage());
        errorDto.setException(exception.getClass().getName());
        errorDto.setTimestamp(Instant.now().toString());
        errorDto.setCorrelationId("MISSING");
        return Response.status(INTERNAL_SERVER_ERROR)
                .entity(errorDto)
                .build();
    }
}
