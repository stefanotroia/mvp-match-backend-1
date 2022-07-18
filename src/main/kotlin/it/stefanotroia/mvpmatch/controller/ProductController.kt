package it.stefanotroia.mvpmatch.controller

import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import it.stefanotroia.mvpmatch.controller.swagger.ProductSwagger
import it.stefanotroia.mvpmatch.model.product.ProductModel
import it.stefanotroia.mvpmatch.service.ProductService
import it.stefanotroia.mvpmatch.utils.repository.PageResponse
import it.stefanotroia.mvpmatch.utils.toUUID
import java.security.Principal
import java.util.*
import javax.validation.Valid

@Controller("/api/v1/products")
@Secured(SecurityRule.IS_AUTHENTICATED)
open class ProductController(private val productService: ProductService): ProductSwagger {

    @Get
    @Secured(*["SELLER", "BUYER"])
    @Status(HttpStatus.OK)
    override fun listProducts(
        @QueryValue(value = "offset", defaultValue = "0") offset: Int,
        @QueryValue(value = "limit", defaultValue = "10") limit: Int,
    ): PageResponse<ProductModel> {
        return this.productService.listProducts(offset, limit)
    }

    @Get("/{productId}")
    @Secured(*["SELLER", "BUYER"])
    @Status(HttpStatus.OK)
    override fun getProduct(@PathVariable productId: UUID): ProductModel {
        return this.productService.getProductsById(productId)
    }

    @Post
    @Secured(*["SELLER"])
    @Status(HttpStatus.CREATED)
    override  fun createProduct(@Body @Valid product: ProductModel, principal: Principal): ProductModel {
        return this.productService.createProduct(product, principal.name.toUUID())
    }

    @Put("/{productId}")
    @Secured(*["SELLER"])
    @Status(HttpStatus.ACCEPTED)
    override  fun updateProduct(@PathVariable productId: UUID,
                           @Body @Valid product: ProductModel,
                           principal: Principal): ProductModel {
        return this.productService.updateProduct(productId, product, principal.name.toUUID())
    }

    @Put("/{productId}")
    @Secured(*["SELLER"])
    @Status(HttpStatus.NO_CONTENT)
    override  fun deleteProduct(@PathVariable productId: UUID,
                           principal: Principal) {
        return this.productService.deleteProduct(productId, principal.name.toUUID())
    }
}
