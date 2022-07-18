package it.stefanotroia.mvpmatch.controller.swagger

import io.micronaut.http.annotation.Body
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import it.stefanotroia.mvpmatch.controller.dto.BuyRequest
import it.stefanotroia.mvpmatch.controller.dto.BuyResponse
import it.stefanotroia.mvpmatch.controller.dto.DepositRequest
import it.stefanotroia.mvpmatch.controller.dto.DepositResponse
import it.stefanotroia.mvpmatch.utils.exceptions.ExceptionResponse
import java.security.Principal
import javax.validation.Valid

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
    ApiResponse(
        description = "bad request",
        responseCode = "400",
        content = [Content(schema = Schema(implementation = ExceptionResponse::class, description = "error"))]
    ),
)
interface SellingMachineSwagger {



    @Operation(
        tags = ["selling-machine"],
        summary = "Deposit coin in user account",
        parameters = [Parameter(
            name = "Authorization",
            content = [Content(schema = Schema(implementation = String::class, description = "Bearer Token"))],
            `in` = ParameterIn.HEADER,
            required = true
        )]
    )
    @ApiResponses(
        ApiResponse(
            description = "response",
            content = [Content(schema = Schema(implementation = DepositResponse::class, description = "response"))]
        ),
    )
     fun deposit(@Body @Valid deposit: DepositRequest,
                 @Parameter(hidden = true)principal: Principal): DepositResponse

    @Operation(
        tags = ["selling-machine"],
        summary = "Buy product",
        parameters = [Parameter(
            name = "Authorization",
            content = [Content(schema = Schema(implementation = String::class, description = "Bearer Token"))],
            `in` = ParameterIn.HEADER,
            required = true
        )]
    )
    @ApiResponses(
        ApiResponse(
            description = "response",
            content = [Content(schema = Schema(implementation = BuyResponse::class, description = "response"))]
        ),
        ApiResponse(
            description = "not found",
            responseCode = "404",
            content = [Content(schema = Schema(implementation = ExceptionResponse::class, description = "error"))]
        ),
    )
     fun buyProduct(@Body @Valid request: BuyRequest,
                     @Parameter(hidden = true) principal: Principal): BuyResponse

    @Operation(
        tags = ["selling-machine"],
        summary = "Reset deposit",
        parameters = [Parameter(
            name = "Authorization",
            content = [Content(schema = Schema(implementation = String::class, description = "Bearer Token"))],
            `in` = ParameterIn.HEADER,
            required = true
        )]
    )
    @ApiResponses(
        ApiResponse(
            description = "response",
            content = [Content(schema = Schema(implementation = Map::class, description = "response"))]
        ),
    )
     fun resetDeposit(@Parameter(hidden = true)principal: Principal): Map<Int,Int>


}
