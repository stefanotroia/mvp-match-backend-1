package it.stefanotroia.mvpmatch.controller.dto

import java.util.*


class BuyResponse(
    val productId: UUID? = null,
    val spentAmount: Int? = null,
    val change: Map<Int, Int> = emptyMap()
)