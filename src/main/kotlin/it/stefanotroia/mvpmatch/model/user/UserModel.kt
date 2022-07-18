package it.stefanotroia.mvpmatch.model.user

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected
import it.stefanotroia.mvpmatch.utils.repository.BaseModel
import java.time.LocalDateTime
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern

@Introspected
class UserModel(
    override var id: UUID? = null,
    override var createdAt: LocalDateTime? = null,
    override var updatedAt: LocalDateTime? = null,
    @field:NotBlank
    @field:Pattern(regexp = "^(?=[a-zA-Z0-9._]{8,20}\$)(?!.*[_.]{2})[^_.].*[^_.]\$")
    var username: String? = null,
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @field:Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*#?&])[A-Za-z\\d@\$!%*#?&]{8,}\$")
    var password: String? = null,
    @field:NotNull
    var role: UserRole? = null,
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    var deposit: Int? = null,
) : BaseModel(id, createdAt, updatedAt)

enum class UserRole {
    BUYER,
    SELLER,
}