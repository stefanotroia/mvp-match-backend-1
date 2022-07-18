package it.stefanotroia.mvpmatch.service

import io.micronaut.http.HttpStatus
import it.stefanotroia.mvpmatch.acceptedCoins
import it.stefanotroia.mvpmatch.controller.dto.BuyRequest
import it.stefanotroia.mvpmatch.controller.dto.BuyResponse
import it.stefanotroia.mvpmatch.controller.dto.DepositResponse
import it.stefanotroia.mvpmatch.model.sellingMachine.SellingMachineModel
import it.stefanotroia.mvpmatch.model.sellingMachine.SellingMachineTable
import it.stefanotroia.mvpmatch.utils.exceptions.ExceptionResponse
import it.stefanotroia.mvpmatch.utils.repository.ExposedRepository
import jakarta.inject.Singleton
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

@Singleton
class SellingMachineService(
    private val userService: UserService,
    private val productService: ProductService,

    ) {

    private val exp: ExposedRepository<SellingMachineTable, SellingMachineModel> =
        ExposedRepository(SellingMachineTable, SellingMachineModel())


    fun depositCoin(coin: Int, userId: UUID): DepositResponse {
        return transaction {
            val user = userService.findUserById(userId)
            val result = DepositResponse((user.deposit ?: 0).plus(coin))
            userService.updateAmount(userId, result.deposit!!, this)
            updateCoinRecord(coin, 1)
            result
        }
    }

    /*  Assuming that it's possible to buy a product only if machine has enough coins for the change, if not
        the user will be notified with an error, and he will be able to get coins back with /reset.
        After purchasing, the change will be computed, the user will receive coins back and deposit will be reset to 0
     */
    fun buyProduct(request: BuyRequest, userId: UUID): BuyResponse {
        return transaction {
            val product = productService.getProductsById(request.productId!!)
            val productPrice = product.cost!!.times(request.quantity!!)

            //check if stock can satisfy requested quantity
            if (product.inStock!! < request.quantity!!)
                throw ExceptionResponse(HttpStatus.BAD_REQUEST, "Product not in stock", "no_stock_for_product")

            //check if user deposit can satisfy requested quantity
            val user = userService.findUserById(userId, true)

            if ((user.deposit ?: 0) < productPrice)
                throw ExceptionResponse(HttpStatus.BAD_REQUEST, "Price too high", "bad_deposit")

            //compute change, purchase will fail if change is not possible
            val change = user.deposit!! - productPrice
            var changeCoins: Map<Int, Int> = emptyMap()

            changeCoins = updateCoinsAndUserAmount(userId, change, this)

            //decrease product quantity
            productService.updateProductStock(product.id!!, product.inStock!!.minus(request.quantity!!), this)

            BuyResponse(request.productId, productPrice, changeCoins)
        }
    }


    /*
        Reset it's possibile only if machine has enough coins and if the user deposit is > 0
     */
    fun resetDeposit(userId: UUID): Map<Int, Int> {
        return transaction {
            val user = userService.findUserById(userId, true)

            if ((user.deposit ?: 0) <= 0) {
                throw ExceptionResponse(HttpStatus.BAD_REQUEST, "nothing to reset", "no_deposit")
            }

            updateCoinsAndUserAmount(userId, user.deposit!!, this)
        }
    }

    private fun updateCoinsAndUserAmount(userId: UUID, amount: Int, t: Transaction): Map<Int, Int> {
        val coinList = getCoinRows()
        val changeCoins = computeChange(amount, coinList)
        changeCoins.forEach {
            coinList.first { r -> r.coin == it.key }.let { row ->
                exp.update(row.id!!, row, t)
            }
        }

        userService.updateAmount(userId, 0, t)
        return changeCoins
    }

    fun computeChange(amount: Int, coinList: List<SellingMachineModel>): Map<Int, Int> {
        var changeMap = mapOf<Int, Int>()
        var remainingAmount = amount

        coinList.sortedByDescending { it.coin }.forEach { coinRow ->
            coinRow.coin?.let { coin ->
                val r = computeChangeFromCoin(coin, coinRow, remainingAmount, changeMap)
                remainingAmount = r.first
                changeMap = r.second
            }
        }

        if (remainingAmount != 0)
            throw ExceptionResponse(HttpStatus.BAD_REQUEST, "No change for amount: ${amount}", "no_change")

        return changeMap
    }

    private fun computeChangeFromCoin(
        coin: Int,
        coinRow: SellingMachineModel,
        amount: Int,
        map: Map<Int, Int>
    ): Pair<Int, Map<Int,Int>> {
        var changeMap = map
        var remainingAmount = amount
        if (coin <= remainingAmount && (coinRow.amount ?: 0) > 0) {
            remainingAmount = remainingAmount.minus(coin)
            coinRow.amount = coinRow.amount!! - 1
            changeMap = if (changeMap.containsKey(coin))
                changeMap.plus(coin to changeMap[coin]!! + 1)
            else
                changeMap.plus(coin to 1)
            if(coinRow.amount!! > 0)
                return computeChangeFromCoin(coin, coinRow, remainingAmount, changeMap)
        }
        return (remainingAmount to changeMap)
    }

    private fun updateCoinRecord(coin: Int, action: Int) {
        SellingMachineTable.update({ SellingMachineTable.coin eq coin }) {
            with(SqlExpressionBuilder) {
                it.update(amount, amount + action)
            }
        }
    }

    fun getCoinRows(): List<SellingMachineModel> {
        return transaction {
            val filter = Op.build { SellingMachineTable.amount greaterEq 1 }
            exp.findByQuery(SellingMachineTable.select(filter), 0, acceptedCoins.size).items
        }
    }


}