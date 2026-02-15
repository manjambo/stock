package com.gaywood.stock.domain.stock.model

data class LowStockThreshold(val quantity: Quantity) {

    fun isBreached(currentQuantity: Quantity): Boolean {
        return currentQuantity.isLessThanOrEqualTo(quantity)
    }

    override fun toString(): String = "Low stock threshold: $quantity"
}
