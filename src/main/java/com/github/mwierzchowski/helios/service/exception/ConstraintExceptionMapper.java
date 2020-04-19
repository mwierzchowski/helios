package com.github.mwierzchowski.helios.service.exception;

import com.github.mwierzchowski.helios.service.dto.ErrorDto;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.status;

/**
 * Exception mapper for translating {@link ConstraintViolationException} to
 * {@link com.github.mwierzchowski.helios.service.dto.ErrorDto}.
 * @author Marcin Wierzchowski
 */
@Provider
@Component
public class ConstraintExceptionMapper implements ExceptionMapper<ConstraintViolationException>  {
    /**
     * Main mapper method
     * @param exception exception
     * @return response
     */
    @Override
    public Response toResponse(ConstraintViolationException exception) {
        var violations = exception.getConstraintViolations();
        var errorDtoList = violations.stream()
                .map(this::toErrorDto)
                .collect(toList());
        return status(BAD_REQUEST)
                .type(APPLICATION_JSON_TYPE)
                .entity(errorDtoList)
                .build();
    }

    /**
     * Helper method that builds error DTO based on violation
     * @param violation violation
     * @return error DTO
     */
    private ErrorDto toErrorDto(ConstraintViolation<?> violation) {
        var errorDto = new ErrorDto();
        errorDto.setMessage(violation.getMessage());
        errorDto.setObject(violation.getPropertyPath().toString());
        errorDto.setValue(violation.getInvalidValue());
        return errorDto;
    }
}
