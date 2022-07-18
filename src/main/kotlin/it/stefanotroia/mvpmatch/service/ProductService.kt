package it.stefanotroia.mvpmatch.service

import io.micronaut.http.HttpStatus
import it.stefanotroia.mvpmatch.model.product.ProductModel
import it.stefanotroia.mvpmatch.model.product.ProductTable
import it.stefanotroia.mvpmatch.utils.exceptions.ExceptionResponse
import it.stefanotroia.mvpmatch.utils.repository.ExposedRepository
import it.stefanotroia.mvpmatch.utils.repository.PageResponse
import it.stefanotroia.mvpmatch.utils.security.BCryptPasswordEncoderService
import jakarta.inject.Singleton
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

@Singleton
class ProductService(private val encoderService: BCryptPasswordEncoderService) {

    private val exp: ExposedRepository<ProductTable, ProductModel> = ExposedRepository(ProductTable, ProductModel())

    fun createProduct(product: ProductModel, sellerId: UUID): ProductModel {
        return transaction {
            product.sellerId = sellerId

            findProductByName(product.name!!)?.let {
                throw ExceptionResponse(
                    HttpStatus.CONFLICT,
                    "Product already exists with name ${it.name}",
                    "product_conflict"
                )
            }

            exp.save(product, this)
        }
    }

    //During the update, a lock will be applied on product row, to avoid a product to bought during the update operation
    fun updateProduct(productId: UUID, product: ProductModel, sellerId: UUID): ProductModel {
        return transaction {
            val persisted = getProductsById(productId, true)

            if (persisted.name != product.name) {
                findProductByName(product.name!!)?.let {
                    throw ExceptionResponse(
                        HttpStatus.CONFLICT,
                        "Product already exists with name ${it.name}",
                        "product_conflict"
                    )
                }
            }

            if (persisted.sellerId != sellerId) {
                throw ExceptionResponse(HttpStatus.FORBIDDEN, "Product owned by another seller", "forbidden")
            }
            product.sellerId = sellerId

            exp.update(productId, product, this)
        }
    }

    //During delete, a lock will be applied on product row, to avoid a product to bought during the delete operation
    fun deleteProduct(productId: UUID, sellerId: UUID) {
        return transaction {
            val persisted = getProductsById(productId, true)

            if (persisted.sellerId != sellerId) {
                throw ExceptionResponse(HttpStatus.FORBIDDEN, "Product owned by another seller", "forbidden")
            }

            exp.delete(productId)
        }
    }

    //During delete, a lock will be applied on seller's products, to avoid products to bought during the delete operation
    fun deleteProductsBySellerId(sellerId: UUID) {
        val products = ProductTable.select { ProductTable.sellerId eq sellerId }.forUpdate()
        exp.delete(Op.build { ProductTable.sellerId eq sellerId })
    }


    fun listProducts(offset: Int, limit: Int): PageResponse<ProductModel> {
        return transaction {
            exp.findByQuery(ProductTable.selectAll(), offset, limit)
        }

    }

    fun getProductsById(id: UUID, forUpdate: Boolean = false): ProductModel {
        return transaction {
            exp.findFirstBy(Op.build { ProductTable.id eq id }, forUpdate = forUpdate) ?: throw ExceptionResponse(
                HttpStatus.NOT_FOUND,
                "Product not found",
                "not_found"
            )
        }
    }

    fun updateProductStock(id: UUID, newStock: Int, t: Transaction) {
        exp.update(Op.build { ProductTable.id eq id }, ProductModel().apply { inStock = newStock }, t, patch = true)
    }

    private fun findProductByName(name: String): ProductModel? {
        return exp.findFirstBy(Op.build { ProductTable.name eq name })
    }

}
