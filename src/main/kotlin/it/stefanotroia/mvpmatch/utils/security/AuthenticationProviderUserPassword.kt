package it.stefanotroia.mvpmatch.utils.security

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.security.authentication.AuthenticationProvider
import io.micronaut.security.authentication.AuthenticationRequest
import io.micronaut.security.authentication.AuthenticationResponse
import it.stefanotroia.mvpmatch.service.SessionService
import it.stefanotroia.mvpmatch.service.UserService
import it.stefanotroia.mvpmatch.utils.exceptions.ExceptionResponse
import jakarta.inject.Singleton
import org.reactivestreams.Publisher
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import java.util.*

@Singleton
class AuthenticationProviderUserPassword(
    private val userService: UserService,
    private val encoderService: BCryptPasswordEncoderService,
    private val sessionService: SessionService,
) : AuthenticationProvider {

    override fun authenticate(
        httpRequest: HttpRequest<*>?,
        authenticationRequest: AuthenticationRequest<*, *>
    ): Publisher<AuthenticationResponse> {
        return Flux.create({ emitter: FluxSink<AuthenticationResponse> ->
            userService.findUserByUsername(authenticationRequest.identity.toString())?.let { user ->
                if (this.encoderService.matches(authenticationRequest.secret.toString(), user.password)) {
                    val jit = UUID.randomUUID()
                    val duration = 60 * 60 * 1_000

                    sessionService.createSession(user.id!!, jit, System.currentTimeMillis().plus(duration))
                    emitter.next(AuthenticationResponse.success(
                            user.id.toString(),
                            listOf(user.role!!.name),
                            mapOf(
                                "username" to user.username,
                                "jit" to jit.toString()
                            )
                        ))
                } else {
                    emitter.error(ExceptionResponse(HttpStatus.NOT_FOUND, "wrong username or password"))
                }
            } ?: run {
                emitter.error(ExceptionResponse(HttpStatus.NOT_FOUND, "wrong username or password"))
            }
            emitter.complete()
        }, FluxSink.OverflowStrategy.ERROR)
    }
}