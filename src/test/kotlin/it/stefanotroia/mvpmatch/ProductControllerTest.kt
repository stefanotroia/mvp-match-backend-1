package it.stefanotroia.mvpmatch

import io.micronaut.http.HttpStatus
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import it.stefanotroia.mvpmatch.configuration.DataSourceConfiguration
import it.stefanotroia.mvpmatch.controller.ProductController
import it.stefanotroia.mvpmatch.model.product.ProductModel
import it.stefanotroia.mvpmatch.model.user.UserModel
import it.stefanotroia.mvpmatch.model.user.UserRole
import it.stefanotroia.mvpmatch.service.UserService
import it.stefanotroia.mvpmatch.utils.exceptions.ExceptionResponse
import jakarta.inject.Inject
import org.apache.http.auth.BasicUserPrincipal
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import java.util.*
import javax.validation.ConstraintViolationException


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest
@Order(1)
@TestMethodOrder(OrderAnnotation::class)
class ProductControllerTest {

    @Inject
    lateinit var productController: ProductController

    @Inject
    lateinit var userService: UserService

    @Inject
    var dataSourceConfiguration: DataSourceConfiguration? = null
    lateinit var sellerId: UUID
    lateinit var productId: UUID


    @BeforeAll
    fun createSellerUser() {
        transaction {
            //create buyer user
            userService.createUser(UserModel().apply {
                role = UserRole.SELLER
                password = "seller"
                username = "seller"
            }).let { sellerId = it.id!! }
        }
    }

    @Test
    @Order(1)
    fun product_create() {
        val res = productController.createProduct(
            ProductModel().apply {
                name = "Candies"
                inStock = 2
                cost = 35
                this.sellerId = sellerId
            },
            BasicUserPrincipal(sellerId.toString())
        )

        assert(res.id != null)
        productId = res.id!!
    }


    @Test
    @Order(2)
    fun product_createConflict() {
        val err = assertThrows<ExceptionResponse> {
            productController.createProduct(
                ProductModel().apply {
                    name = "Candies"
                    inStock = 2
                    cost = 35
                    this.sellerId = sellerId
                },
                BasicUserPrincipal(sellerId.toString())
            )
        }

        assert(err.statusCode == HttpStatus.CONFLICT)
    }

    @Test
    @Order(3)
    fun product_createBadPrice() {
        assertThrows<ConstraintViolationException> {
            productController.createProduct(
                ProductModel().apply {
                    name = "Candies"
                    inStock = 2
                    cost = 36
                    this.sellerId = sellerId
                },
                BasicUserPrincipal(sellerId.toString())
            )
        }
    }

    @Test
    @Order(4)
    fun product_list() {
        val res = productController.listProducts(0, 10)
        assert(res.total != 0)
    }

    @Test
    @Order(5)
    fun product_getSingle() {
        val res = productController.getProduct(productId)

        assert(res.id == productId)
    }

    @Test
    @Order(6)
    fun product_update() {
        val res = productController.updateProduct(productId, ProductModel().apply {
            name = "Candies"
            inStock = 5
            cost = 35
            this.sellerId = sellerId
        }, BasicUserPrincipal(sellerId.toString()))

        assert(res.inStock == 5)
    }

    @Test
    @Order(7)
    fun product_delete() {
        productController.deleteProduct(productId, BasicUserPrincipal(sellerId.toString()))
        val err = assertThrows<ExceptionResponse> {
            productController.getProduct(productId)
        }
        assert(err.statusCode == HttpStatus.NOT_FOUND)
    }

}