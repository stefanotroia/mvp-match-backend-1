package it.stefanotroia.mvpmatch.utils.validators.product

import javax.validation.Constraint


@Constraint(validatedBy = [])
annotation class ProductPrice(val message: String = "invalid product price: ({validatedValue})")