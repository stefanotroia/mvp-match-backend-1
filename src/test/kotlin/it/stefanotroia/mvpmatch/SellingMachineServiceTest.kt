package it.stefanotroia.mvpmatch

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import it.stefanotroia.mvpmatch.configuration.DataSourceConfiguration
import it.stefanotroia.mvpmatch.controller.dto.BuyRequest
import it.stefanotroia.mvpmatch.model.product.ProductModel
import it.stefanotroia.mvpmatch.model.sellingMachine.SellingMachineModel
import it.stefanotroia.mvpmatch.model.user.UserModel
import it.stefanotroia.mvpmatch.model.user.UserRole
import it.stefanotroia.mvpmatch.service.ProductService
import it.stefanotroia.mvpmatch.service.SellingMachineService
import it.stefanotroia.mvpmatch.service.UserService
import it.stefanotroia.mvpmatch.utils.exceptions.ExceptionResponse
import jakarta.inject.Inject
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import java.util.*


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest
@Order(2)
@TestMethodOrder(OrderAnnotation::class)
class SellingMachineServiceTest {

    @Inject
    lateinit var sellingMachineService: SellingMachineService
    @Inject
    lateinit  var userService: UserService
    @Inject
    lateinit  var productService: ProductService
    @Inject
    var dataSourceConfiguration: DataSourceConfiguration? = null
    lateinit var buyerId: UUID
    lateinit var productId: UUID

    @BeforeAll
    fun initProductsAndUsers() {
        transaction {
            //create buyer user
            userService.createUser(UserModel().apply {
                role = UserRole.BUYER
                password = "buyer"
                username = "buyer"
            }).let { buyerId = it.id!! }


            val seller = userService.createUser(UserModel().apply {
                role = UserRole.SELLER
                password = "seller_selling_machine"
                username = "seller_selling_machine"
            })

            productService.createProduct(
                ProductModel().apply {
                    name = "CandiesSellerMachine"
                    inStock = 2
                    cost = 35
                },
                seller.id!!
            ).let { productId = it.id!! }
        }
    }

    @Test
    @Order(1)
    fun deposit_success() {
        val user = userService.findUserById(buyerId)
        assert(user.deposit == 0)

        sellingMachineService.depositCoin(10, buyerId)
        sellingMachineService.depositCoin(20, buyerId)
        sellingMachineService.depositCoin(20, buyerId)
        val res = sellingMachineService.depositCoin(50, buyerId)

        val coins = sellingMachineService.getCoinRows()

        assert(coins.firstOrNull { it.coin == 10 }?.amount == 1)
        assert(coins.firstOrNull { it.coin == 20 }?.amount == 2)
        assert(coins.firstOrNull { it.coin == 50 }?.amount == 1)
        assert(res.deposit == 100)
        assert(userService.findUserById(buyerId).deposit == 100)
    }

    @Test
    @Order(2)
    fun depositReset_amountGreaterThan0() {
        val user = userService.findUserById(buyerId)
        assert(user.deposit != 0)

       sellingMachineService.resetDeposit(buyerId)

        val coins = sellingMachineService.getCoinRows()

        assert(coins.isEmpty())
        assert(userService.findUserById(buyerId).deposit == 0)
    }

    @Test
    @Order(3)
    fun depositReset_noDeposit() {
        val err = assertThrows<ExceptionResponse> {
            sellingMachineService.resetDeposit(buyerId)
        }
        assert(err.errorCode == "no_deposit")
    }

    @Test
    @Order(4)
    fun buy_errorMissingProduct() {
        val err = assertThrows<ExceptionResponse> {
            sellingMachineService.buyProduct(BuyRequest(UUID.randomUUID(), 1), buyerId)
        }
        assert(err.errorCode == "not_found")
    }

    @Test
    @Order(5)
    fun buy_errorUnsatisfiedAmount() {
        val user = userService.findUserById(buyerId)
        assert(user.deposit == 0)

        val err = assertThrows<ExceptionResponse> {
            sellingMachineService.buyProduct(BuyRequest(productId, 1), buyerId)
        }

        assert(err.errorCode == "bad_deposit")
    }

    @Test
    @Order(6)
    fun buy_success() {
        val user = userService.findUserById(buyerId)
        assert(user.deposit == 0)

        //setting deposit to 100
        deposit_success()

        val res = sellingMachineService.buyProduct(BuyRequest(productId, 2), buyerId)
        val coins = sellingMachineService.getCoinRows()

        assert(res.spentAmount == 70)
        assert(res.change.containsKey(20) && res.change.containsKey(10))
        assert(coins.first { it.coin == 20 }.amount == 1)
        assert(coins.firstOrNull { it.coin == 10 } == null)
        assert(coins.first { it.coin == 50 }.amount == 1)
        assert(productService.getProductsById(productId).inStock == 0)
    }



    //change functions
    @Test
    @Order(7)
    fun changeComputation_satisfiedAmounts_1() {
        val change  = sellingMachineService.computeChange(10,
            listOf(SellingMachineModel(coin = 10, amount = 1)))

        assert(change.size == 1)
        assert(change.containsKey(10))
        assert(change[10] == 1)
    }

    @Test
    @Order(8)
    fun changeComputation_satisfiedAmounts_2() {
        val change  = sellingMachineService.computeChange(15,
            listOf(
                SellingMachineModel(coin = 20, amount = 1),
                SellingMachineModel(coin = 5, amount = 3),
                SellingMachineModel(coin = 10, amount = 5),
            ))

        assert(change.size == 2)
        assert(change.containsKey(10) && change.containsKey(5))
        assert(change[10] == 1 && change[5] == 1)
    }

    @Test
    @Order(9)
    fun changeComputation_noAmount() {
        val change  = sellingMachineService.computeChange(0,
            listOf(
                SellingMachineModel(coin = 20, amount = 1),
                SellingMachineModel(coin = 5, amount = 3),
                SellingMachineModel(coin = 10, amount = 5),
            ))

        assert(change.isEmpty())
    }

    @Test
    @Order(10)
    fun changeComputation_errorNoChange() {
        val err = assertThrows<ExceptionResponse> {
            sellingMachineService.computeChange(
                1000,
                listOf(
                    SellingMachineModel(coin = 20, amount = 1),
                    SellingMachineModel(coin = 5, amount = 3),
                    SellingMachineModel(coin = 10, amount = 5),
                )
            )
        }
        assert(err.errorCode == "no_change")
    }

}