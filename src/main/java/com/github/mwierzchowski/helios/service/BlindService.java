package com.github.mwierzchowski.helios.service;

import com.github.mwierzchowski.helios.core.blinds.Blind;
import com.github.mwierzchowski.helios.core.blinds.BlindDriverRegistry;
import com.github.mwierzchowski.helios.core.blinds.BlindMovement;
import com.github.mwierzchowski.helios.core.blinds.BlindRepository;
import com.github.mwierzchowski.helios.core.commons.NotFoundException;
import com.github.mwierzchowski.helios.service.dto.BlindDto;
import com.github.mwierzchowski.helios.service.dto.BlindMoveDto;
import com.github.mwierzchowski.helios.service.dto.RequestErrorDto;
import com.github.mwierzchowski.helios.service.dto.ServiceErrorDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Slf4j
@Validated
@Transactional
@RequiredArgsConstructor
@Tag(name = "Blinds", description = "Blind Service")
@ApiResponse(description = "Success")
@ApiResponse(description = "Bad request", responseCode = "4xx",
        content = @Content(schema = @Schema(implementation = RequestErrorDto.class)))
@ApiResponse(description = "Service failure", responseCode = "5xx",
        content = @Content(schema = @Schema(implementation = ServiceErrorDto.class)))
@Path("/v1/blinds")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class BlindService {
    private final Mapper mapper = Mappers.getMapper(Mapper.class);
    private final BlindDriverRegistry registry;
    private final BlindRepository repository;

    @GET
    @Operation(summary = "List of blinds", description = "Provides list of available blinds")
    public List<BlindDto> getBlinds() {
        log.debug("Looking for blinds...");
        return repository.findAll().stream()
                .map(mapper::toBlindDto)
                .collect(toList());
    }

    @PUT
    @Path("/{id}")
    public BlindMoveDto move(@PathParam("id") Integer id, @NotNull @Min(0) @Max(100) Integer stopPosition) {
        log.debug("Moving blind {} to position {}...", id, stopPosition);
        var blind = repository.findById(id).orElseThrow(() -> new NotFoundException(Blind.class, id));
        var driver = registry.driverFor(blind);
        var movement = blind.move(driver, stopPosition);
        log.info("Blind {} will move to position {} in {}ms", id, stopPosition, movement.getTime());
        return mapper.toBlindMoveDto(movement);
    }

    @PUT
    @Path("/{id}/stop")
    public BlindDto stop(@PathParam("id") Integer id) {
        log.debug("Stopping blind {}...", id);
        var blind = repository.findById(id).orElseThrow(() -> new NotFoundException(Blind.class, id));
        var driver = registry.driverFor(blind);
        blind.stop(driver);
        log.info("Blind {} was stopped at position {}", id, blind.getPosition());
        return mapper.toBlindDto(blind);
    }


    @org.mapstruct.Mapper
    interface Mapper {
        BlindDto toBlindDto(Blind blind);
        BlindMoveDto toBlindMoveDto(BlindMovement blindMovement);
    }

}
