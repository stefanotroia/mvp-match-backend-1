package it.stefanotroia.mvpmatch.model.session

import io.micronaut.core.annotation.Introspected
import it.stefanotroia.mvpmatch.utils.repository.BaseModel
import java.time.LocalDateTime
import java.util.*

@Introspected
class SessionModel(
    override var id: UUID? = null,
    override var createdAt: LocalDateTime? = null,
    override var updatedAt: LocalDateTime? = null,

    var userId: UUID? = null,
    var jit: UUID? = null,
    var revoked: Boolean? = false,
    var exp: Long? = null,

) : BaseModel(id, createdAt, updatedAt)

