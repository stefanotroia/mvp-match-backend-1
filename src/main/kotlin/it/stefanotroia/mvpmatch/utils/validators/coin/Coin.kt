package it.stefanotroia.mvpmatch.utils.validators.coin

import javax.validation.Constraint


@Constraint(validatedBy = [])
annotation class Coin(val message: String = "invalid coin: ({validatedValue})")