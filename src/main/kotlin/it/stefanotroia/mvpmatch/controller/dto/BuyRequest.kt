package it.stefanotroia.mvpmatch.controller.dto

import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

@Introspected
class BuyRequest(
    @field:NotNull
    var productId: UUID? = null,
    @field:NotNull
    @field:Min(1)
    var quantity: Int? = null
)