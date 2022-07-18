package it.stefanotroia.mvpmatch.controller

import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import it.stefanotroia.mvpmatch.controller.dto.BuyRequest
import it.stefanotroia.mvpmatch.controller.dto.BuyResponse
import it.stefanotroia.mvpmatch.controller.dto.DepositRequest
import it.stefanotroia.mvpmatch.controller.dto.DepositResponse
import it.stefanotroia.mvpmatch.controller.swagger.SellingMachineSwagger
import it.stefanotroia.mvpmatch.service.SellingMachineService
import it.stefanotroia.mvpmatch.utils.toUUID
import java.security.Principal
import javax.validation.Valid

@Controller("/api/v1/selling-machine")
open class SellingMachineController(private val service: SellingMachineService): SellingMachineSwagger {


    @Post("/deposit")
    @Secured(*["BUYER"])
    @Status(HttpStatus.CREATED)
    override fun deposit(@Body @Valid deposit: DepositRequest,
                     principal: Principal): DepositResponse {
       return  service.depositCoin(deposit.coin!!, principal.name.toUUID())
    }

    @Post("/buy")
    @Secured(*["BUYER"])
    @Status(HttpStatus.CREATED)
    override fun buyProduct(@Body @Valid request: BuyRequest,
                     principal: Principal): BuyResponse {
        return  service.buyProduct(request, principal.name.toUUID())
    }

    @Put("/reset")
    @Secured(*["BUYER"])
    @Status(HttpStatus.ACCEPTED)
    override fun resetDeposit(principal: Principal): Map<Int,Int> {
        return  service.resetDeposit(principal.name.toUUID())
    }


}
