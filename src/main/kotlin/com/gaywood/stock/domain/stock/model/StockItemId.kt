package com.gaywood.stock.domain.stock.model

import java.util.UUID

@JvmInline
value class StockItemId(val value: String) {
    companion object {
        fun generate(): StockItemId = StockItemId(UUID.randomUUID().toString())
    }

    override fun toString(): String = value
}
