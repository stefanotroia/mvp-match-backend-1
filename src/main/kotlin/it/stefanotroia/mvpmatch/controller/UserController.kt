package it.stefanotroia.mvpmatch.controller

import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import it.stefanotroia.mvpmatch.controller.swagger.UserSwagger
import it.stefanotroia.mvpmatch.model.user.UserModel
import it.stefanotroia.mvpmatch.service.UserService
import it.stefanotroia.mvpmatch.utils.exceptions.ExceptionResponse
import it.stefanotroia.mvpmatch.utils.toUUID
import java.security.Principal
import java.util.*
import javax.annotation.security.PermitAll
import javax.validation.Valid

@Controller("/api/v1/users")
@Secured(SecurityRule.IS_AUTHENTICATED)
open class UserController(private val userService: UserService): UserSwagger {

    @Post
    @PermitAll
    @Status(HttpStatus.CREATED)
    override fun userRegistration(@Body @Valid user: UserModel): UserModel {
        return this.userService.createUser(user)
    }

    @Get("/{id}")
    @Status(HttpStatus.OK)
    override fun getUserById(@PathVariable id: UUID, principal: Principal): UserModel {
        if (principal.name.toUUID() != id) {
            throw ExceptionResponse(HttpStatus.FORBIDDEN, "Cannot read other another user profile", "forbidden")
        }
        return userService.findUserById(id).apply { password = null }
    }

    @Put("/{id}")
    @Status(HttpStatus.ACCEPTED)
    override fun  updateAccount(@PathVariable id: UUID,
                      @Body user: UserModel,
                      @QueryValue(value = "confirmPassword", defaultValue = "false") confirmPassword: Boolean?,
                      principal: Principal): UserModel {
        if (principal.name.toUUID() != id) {
            throw ExceptionResponse(HttpStatus.FORBIDDEN, "Cannot updated other another user profile", "forbidden")
        }
        return userService.updateUser(id, user, confirmPassword)
    }

    @Delete("/{id}")
    @Status(HttpStatus.NO_CONTENT)
    override fun deleteAccount(@PathVariable id: UUID, principal: Principal) {
        if (principal.name.toUUID() != id) {
            throw ExceptionResponse(HttpStatus.FORBIDDEN, "Cannot delete other another user profile", "forbidden")
        }
        return userService.deleteUser(id)
    }

}
