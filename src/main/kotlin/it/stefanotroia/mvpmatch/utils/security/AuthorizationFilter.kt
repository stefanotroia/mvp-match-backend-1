package it.stefanotroia.mvpmatch.utils.security

import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.Claim
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Filter
import io.micronaut.http.filter.HttpServerFilter
import io.micronaut.http.filter.ServerFilterChain
import it.stefanotroia.mvpmatch.service.SessionService
import it.stefanotroia.mvpmatch.utils.exceptions.ExceptionResponse
import it.stefanotroia.mvpmatch.utils.toUUID
import org.reactivestreams.Publisher
import reactor.core.publisher.Mono

@Filter("/api/**")
class AuthorizationFilter(
    private val service: SessionService
) : HttpServerFilter {


    override fun doFilter(request: HttpRequest<*>, chain: ServerFilterChain): Publisher<MutableHttpResponse<*>> {

        getBearerTokenFromRequest(request)?.let {
           val jit =  it["jit"] ?: throw ExceptionResponse(HttpStatus.BAD_REQUEST, "Missing JIT claim", "bad_jwt")
            service.findByJit(jit.asString().toUUID()).takeIf { it.revoked == true }
                ?.let { throw ExceptionResponse(HttpStatus.UNAUTHORIZED, "This token has been revoked", "revoked_token") }
        }

        return Mono.from(chain.proceed(request))
    }

    fun getBearerTokenFromRequest(request: HttpRequest<*>): Map<String, Claim>? {
        var token: String? = request.headers["Authorization"]
        return token?.let {
            val tk = it.replace("Bearer", "").trim()
            return JWT.decode(tk).claims
        }
    }


}
