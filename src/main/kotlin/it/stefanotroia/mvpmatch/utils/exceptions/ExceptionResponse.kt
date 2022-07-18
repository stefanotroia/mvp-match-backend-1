package it.stefanotroia.mvpmatch.utils.exceptions

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.micronaut.http.HttpStatus
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

//@JsonIgnoreProperties(value = ["stackTrace", "localizedMessage"])

@JsonDeserialize(`as` = ExceptionResponse::class)
@Schema(allOf = [ExceptionResponse::class])
class ExceptionResponse(
    var statusCode: HttpStatus,
    override var message: String? = null,
    var errorCode: String? = null,
) : RuntimeException() {
    var origin: String? = null
    var timestamp: LocalDateTime = LocalDateTime.now()
    constructor() : this (HttpStatus.INTERNAL_SERVER_ERROR)
}
