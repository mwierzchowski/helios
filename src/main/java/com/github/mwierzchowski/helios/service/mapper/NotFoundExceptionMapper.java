package com.github.mwierzchowski.helios.service.mapper;

import com.github.mwierzchowski.helios.core.commons.NotFoundException;
import com.github.mwierzchowski.helios.service.dto.RequestErrorDto;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.status;

/**
 * Exception mapper for translating {@link NotFoundException} to {@link RequestErrorDto}.
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
        var errorDto = new RequestErrorDto();
        errorDto.setMessage(exception.getMessage());
        errorDto.setObject(exception.getClazz().getSimpleName());
        errorDto.setValue(exception.getId());
        return status(NOT_FOUND)
                .type(APPLICATION_JSON_TYPE)
                .entity(errorDto)
                .build();
    }
}
