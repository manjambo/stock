package com.gaywood.stock.domain.order.model

import java.util.*

@JvmInline
value class OrderId(val value: String) {
    init {
        require(value.isNotBlank()) { "Order ID cannot be blank" }
    }

    companion object {
        fun generate(): OrderId = OrderId(UUID.randomUUID().toString())

        private val UUID_REGEX = Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")

        fun isValidUuid(value: String): Boolean = UUID_REGEX.matches(value)
    }

    override fun toString(): String = value
}
