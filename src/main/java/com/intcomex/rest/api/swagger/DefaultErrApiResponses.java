package com.intcomex.rest.api.swagger;

import com.intcomex.rest.api.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.lang.annotation.*;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "409",
                description = "Conflicto: intento de crear un recurso que ya existe.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        )
})
public @interface DefaultErrApiResponses {}
