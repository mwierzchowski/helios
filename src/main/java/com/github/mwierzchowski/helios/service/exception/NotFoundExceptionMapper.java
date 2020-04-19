package com.github.mwierzchowski.helios.service.exception;

import com.github.mwierzchowski.helios.core.NotFoundException;
import com.github.mwierzchowski.helios.service.dto.ErrorDto;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.status;

/**
 * Exception mapper for translating {@link NotFoundException} to
 * {@link com.github.mwierzchowski.helios.service.dto.ErrorDto}.
 * @author Marcin Wierzchowski
 */
@Provider
@Component
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {
    /**
     * Main mapper method
     * @param exception exception
     * @return response
     */
    @Override
    public Response toResponse(NotFoundException exception) {
        var errorDto = new ErrorDto();
        errorDto.setMessage(exception.getMessage());
        errorDto.setObject(exception.getClazz().getSimpleName());
        errorDto.setValue(exception.getId());
        return status(NOT_FOUND)
                .type(APPLICATION_JSON_TYPE)
                .entity(errorDto)
                .build();
    }
}
