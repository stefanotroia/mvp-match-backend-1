package it.stefanotroia.mvpmatch.controller.swagger

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import it.stefanotroia.mvpmatch.utils.exceptions.ExceptionResponse
import java.security.Principal


@ApiResponses(
    ApiResponse(
        description = "unauthorized",
        responseCode = "401",
        content = [Content(schema = Schema(implementation = ExceptionResponse::class, description = "error"))]
    ),
    ApiResponse(
        description = "forbidden",
        responseCode = "403",
        content = [Content(schema = Schema(implementation = ExceptionResponse::class, description = "error"))]
    ),
)
interface SessionSwagger {

    @Operation(
        tags = ["session"],
        summary = "Logout sessions",
        parameters = [Parameter(
            name = "Authorization",
            content = [Content(schema = Schema(implementation = String::class, description = "Bearer Token"))],
            `in` = ParameterIn.HEADER,
            required = true
        )]
    )
    fun logout(@Parameter(hidden = true) principal: Principal)

}
