package it.stefanotroia.mvpmatch.controller.swagger

import io.micronaut.http.annotation.PathVariable
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import it.stefanotroia.mvpmatch.model.user.UserModel
import it.stefanotroia.mvpmatch.utils.exceptions.ExceptionResponse
import java.security.Principal
import java.util.*


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
interface UserSwagger {


    @Operation(
        tags = ["user"],
        summary = "Register user",
    )
    @ApiResponses(
        ApiResponse(
            description = "response",
            content = [Content(schema = Schema(implementation = UserModel::class, description = "response"))]
        ),
        ApiResponse(
            description = "conflict",
            responseCode = "409",
            content = [Content(schema = Schema(implementation = ExceptionResponse::class, description = "error"))]
        ),
    )
    fun userRegistration(user: UserModel): UserModel


    @Operation(
        tags = ["user"],
        summary = "User by id",
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
            content = [Content(schema = Schema(implementation = UserModel::class, description = "response"))]
        ),
        ApiResponse(
            description = "not found",
            responseCode = "404",
            content = [Content(schema = Schema(implementation = ExceptionResponse::class, description = "error"))]
        ),
    )
    fun getUserById( id: UUID, @Parameter(hidden = true) principal: Principal): UserModel


    @Operation(
        tags = ["user"],
        summary = "Update user",
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
            content = [Content(schema = Schema(implementation = UserModel::class, description = "response"))]
        ),
        ApiResponse(
            description = "conflict",
            responseCode = "409",
            content = [Content(schema = Schema(implementation = ExceptionResponse::class, description = "error"))]
        ),
        ApiResponse(
            description = "not found",
            responseCode = "404",
            content = [Content(schema = Schema(implementation = ExceptionResponse::class, description = "error"))]
        ),
    )
    fun updateAccount( id: UUID,
                       user: UserModel,
                      confirmPassword: Boolean?,
                      @Parameter(hidden = true) principal: Principal): UserModel


    @Operation(
        tags = ["user"],
        summary = "Delete user by id",
        parameters = [Parameter(
            name = "Authorization",
            content = [Content(schema = Schema(implementation = String::class, description = "Bearer Token"))],
            `in` = ParameterIn.HEADER,
            required = true
        )]
    )
    @ApiResponses(
        ApiResponse(
            description = "not found",
            responseCode = "404",
            content = [Content(schema = Schema(implementation = ExceptionResponse::class, description = "error"))]
        ),
    )
    fun deleteAccount(@PathVariable id: UUID, principal: Principal)

}
