package it.stefanotroia.mvpmatch.model.sellingMachine

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected
import it.stefanotroia.mvpmatch.utils.repository.BaseModel
import java.time.LocalDateTime
import java.util.*

@Introspected
class SellingMachineModel(
    override var id: UUID? = null,
    override var createdAt: LocalDateTime? = null,
    override var updatedAt: LocalDateTime? = null,
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    var coin: Int? = null,
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    var amount: Int? = null,
) : BaseModel(id, createdAt, updatedAt)
