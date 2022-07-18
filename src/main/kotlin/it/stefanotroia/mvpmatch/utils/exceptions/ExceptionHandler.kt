package it.stefanotroia.mvpmatch.utils.exceptions

import io.micronaut.context.annotation.Replaces
import io.micronaut.core.convert.exceptions.ConversionErrorException
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus.*
import io.micronaut.http.server.exceptions.ExceptionHandler
import io.micronaut.security.authentication.AuthorizationException
import io.micronaut.web.router.exceptions.UnsatisfiedBodyRouteException
import jakarta.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
@Replaces(io.micronaut.http.server.exceptions.ExceptionHandler::class)
open class ExceptionHandler :
    ExceptionHandler<Exception, HttpResponse<Any>> {

    override fun handle(request: HttpRequest<*>, ex: Exception?): HttpResponse<Any>? {
        val body: ExceptionResponse = when (ex) {
            is UnsatisfiedBodyRouteException -> {
                ExceptionResponse(BAD_REQUEST, ex.message, "missing_request_body")
            }
            is AuthorizationException -> {
                ExceptionResponse(
                    if (ex.isForbidden) FORBIDDEN else UNAUTHORIZED,
                    ex.message,
                    if (ex.isForbidden) "FORBIDDEN" else "UNAUTHORIZED",)
            }
            is ConstraintViolationException, is ConversionErrorException -> {
                ExceptionResponse(BAD_REQUEST, ex.message, "validation_not_passed")
            }
            is ExceptionResponse -> {
                ex
            }
            else -> {
                ExceptionResponse(INTERNAL_SERVER_ERROR, ex?.message ?: "Generic Error", "generic_error")
            }
        }

        body.apply {
            if (origin == null) origin = request.uri.path
            stackTrace = emptyArray()
        }

        return HttpResponse
            .status<Any?>(body.statusCode)
            .contentType("application/json")
            .body(body)
    }
}

