package it.stefanotroia.mvpmatch.utils.validators.coin

import io.micronaut.context.annotation.Factory
import io.micronaut.validation.validator.constraints.ConstraintValidator
import it.stefanotroia.mvpmatch.acceptedCoins
import jakarta.inject.Singleton


@Factory
class CoinValidatorFactory {
    @Singleton
    fun coinValidator(): ConstraintValidator<Coin, Int> {
        return ConstraintValidator<Coin, Int> { value, annotationMetadata, context ->
             acceptedCoins.contains(value)
        }
    }
}