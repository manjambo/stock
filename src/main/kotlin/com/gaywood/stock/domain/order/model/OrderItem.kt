package com.gaywood.stock.domain.order.model

import com.gaywood.stock.domain.menu.model.MenuItemId
import com.gaywood.stock.domain.menu.model.Price
import java.util.*

@JvmInline
value class OrderItemId(val value: String) {
    companion object {
        fun generate(): OrderItemId = OrderItemId(UUID.randomUUID().toString())
    }
}

data class OrderItem(
    val id: OrderItemId,
    val menuItemId: MenuItemId,
    val menuItemName: String,
    val quantity: Int,
    val unitPrice: Price,
    val notes: String = ""
) {
    init {
        require(quantity > 0) { "Order item quantity must be positive" }
    }

    val totalPrice: Price
        get() = unitPrice * quantity

    companion object {
        fun create(
            id: OrderItemId = OrderItemId.generate(),
            menuItemId: MenuItemId,
            menuItemName: String,
            quantity: Int,
            unitPrice: Price,
            notes: String = ""
        ): OrderItem = OrderItem(
            id = id,
            menuItemId = menuItemId,
            menuItemName = menuItemName,
            quantity = quantity,
            unitPrice = unitPrice,
            notes = notes
        )
    }
}
