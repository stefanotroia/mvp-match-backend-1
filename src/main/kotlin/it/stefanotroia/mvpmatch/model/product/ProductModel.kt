package it.stefanotroia.mvpmatch.model.product

import io.micronaut.core.annotation.Introspected
import it.stefanotroia.mvpmatch.utils.repository.BaseModel
import it.stefanotroia.mvpmatch.utils.validators.product.ProductPrice
import java.time.LocalDateTime
import java.util.*
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Introspected
class ProductModel(
    override var id: UUID? = null,
    override var createdAt: LocalDateTime? = null,
    override var updatedAt: LocalDateTime? = null,

    @field:NotBlank
    var name: String? = null,

    @field:NotNull
    @field:Min(1)
    var inStock: Int? = null,

    @field:NotNull
    @field:ProductPrice
    var cost: Int? = null,

    var sellerId: UUID? = null


) : BaseModel(id, createdAt, updatedAt)

