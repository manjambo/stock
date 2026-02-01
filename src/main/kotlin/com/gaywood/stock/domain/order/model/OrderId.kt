package com.gaywood.stock.domain.order.model

import java.util.*

@JvmInline
value class OrderId(val value: String) {
    companion object {
        fun generate(): OrderId = OrderId(UUID.randomUUID().toString())
    }
}
