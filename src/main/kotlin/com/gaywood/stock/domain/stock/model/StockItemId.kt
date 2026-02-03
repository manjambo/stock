package com.gaywood.stock.domain.stock.model

import java.util.UUID

@JvmInline
value class StockItemId(val value: String) {
    init {
        require(value.isNotBlank()) { "Stock item ID cannot be blank" }
    }

    companion object {
        fun generate(): StockItemId = StockItemId(UUID.randomUUID().toString())

        private val UUID_REGEX = Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")

        fun isValidUuid(value: String): Boolean = UUID_REGEX.matches(value)
    }

    override fun toString(): String = value
}
