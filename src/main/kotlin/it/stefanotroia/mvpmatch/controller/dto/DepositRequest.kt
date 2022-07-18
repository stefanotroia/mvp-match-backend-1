package it.stefanotroia.mvpmatch.controller.dto

import io.micronaut.core.annotation.Introspected
import it.stefanotroia.mvpmatch.utils.validators.coin.Coin
import javax.validation.constraints.NotNull

@Introspected
class DepositRequest(
    @field:NotNull
    @field:Coin
    var coin: Int? = 0
)