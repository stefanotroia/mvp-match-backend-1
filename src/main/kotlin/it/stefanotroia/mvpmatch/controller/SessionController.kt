package it.stefanotroia.mvpmatch.controller

import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Status
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import it.stefanotroia.mvpmatch.controller.swagger.SessionSwagger
import it.stefanotroia.mvpmatch.service.SessionService
import it.stefanotroia.mvpmatch.utils.toUUID
import java.security.Principal
import javax.annotation.security.PermitAll

@Controller
@Secured(SecurityRule.IS_AUTHENTICATED)
open class SessionController(private val sessionService: SessionService): SessionSwagger {

    @Post("/api/v1/logout")
    @PermitAll
    @Status(HttpStatus.NO_CONTENT)
    //logout will revoke all active jwt for current user
    override fun logout(principal: Principal) {
        return sessionService.logout(principal.name.toUUID())
    }


}
