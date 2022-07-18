package it.stefanotroia.mvpmatch

import io.micronaut.runtime.Micronaut.build
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import it.stefanotroia.mvpmatch.utils.annotations.EagerInit


val acceptedCoins = listOf(5,10,20,50,100)
fun main(args: Array<String>) {
	build()
		.eagerInitAnnotated(EagerInit::class.java)
	    .args(*args)
		.packages("it.stefanotroia.mvpmatch")
		.start()
}

@OpenAPIDefinition(
	info = Info(
		title = "MVP Match - Backend#1",
		version = "0.1",
		description = "API for a vending machine"
	),

)
class SwaggerConfig