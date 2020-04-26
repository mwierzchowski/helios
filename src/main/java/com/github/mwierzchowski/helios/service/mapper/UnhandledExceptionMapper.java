package com.github.mwierzchowski.helios.service.mapper;

import com.github.mwierzchowski.helios.core.commons.CommonProperties;
import com.github.mwierzchowski.helios.service.dto.ServiceErrorDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static java.time.LocalDateTime.now;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

/**
 * Exception mapper for translating general {@link Exception} to {@link ServiceErrorDto}.
 * @author Marcin Wierzchowski
 */
@Slf4j
@Provider
@Component
@RequiredArgsConstructor
public class UnhandledExceptionMapper implements ExceptionMapper<Exception> {
    /**
     * Timestamp formatter
     */
    private final CommonProperties commonProperties;

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
        errorDto.setTimestamp(commonProperties.timeFormatter().format(now()));
        errorDto.setCorrelationId("MISSING");
        return Response.status(INTERNAL_SERVER_ERROR)
                .entity(errorDto)
                .build();
    }
}
