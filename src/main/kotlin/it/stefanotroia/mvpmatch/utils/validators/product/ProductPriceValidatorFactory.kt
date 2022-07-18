package it.stefanotroia.mvpmatch.utils.validators.product

import io.micronaut.context.annotation.Factory
import io.micronaut.validation.validator.constraints.ConstraintValidator
import jakarta.inject.Singleton


@Factory
class ProductPriceValidatorFactory {
    @Singleton
    fun productPriceValidator(): ConstraintValidator<ProductPrice, Int> {
        return ConstraintValidator<ProductPrice, Int> { value, annotationMetadata, context ->
            (value % 5) == 0
        }
    }
}