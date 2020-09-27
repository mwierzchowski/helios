package com.github.mwierzchowski.helios.service;

import com.github.mwierzchowski.helios.core.blinds.Blind;
import com.github.mwierzchowski.helios.service.dto.RequestErrorDto;
import com.github.mwierzchowski.helios.service.dto.ServiceErrorDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.Collections;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Slf4j
@Service
@Validated
@Transactional
@RequiredArgsConstructor
@Tag(name = "Blinds", description = "Blinds management")
@ApiResponse(description = "Success")
@ApiResponse(description = "Bad request", responseCode = "4xx",
        content = @Content(schema = @Schema(implementation = RequestErrorDto.class)))
@ApiResponse(description = "Service failure", responseCode = "5xx",
        content = @Content(schema = @Schema(implementation = ServiceErrorDto.class)))
@Path("/v1/blinds")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class BlindService {
    @GET
    @Operation(summary = "List of blinds", description = "Provides list of available blinds")
    public List<Blind> getBlinds() {
        // TODO
        return Collections.emptyList();
    }
}
