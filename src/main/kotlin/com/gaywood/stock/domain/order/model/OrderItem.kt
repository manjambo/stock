package com.gaywood.stock.domain.order.model

import com.gaywood.stock.domain.menu.model.MenuItemId
import com.gaywood.stock.domain.menu.model.Price
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.util.*

@JvmInline
value class OrderItemId(val value: String) {
    init {
        require(value.isNotBlank()) { "Order item ID cannot be blank" }
    }

    companion object {
        fun generate(): OrderItemId = OrderItemId(UUID.randomUUID().toString())
    }
}

/**
 * Represents an item within an order.
 *
 * **Snapshot Pattern**: OrderItem intentionally stores denormalized data
 * ([menuItemName], [unitPrice]) rather than referencing the MenuItem directly.
 * This ensures orders preserve historical accuracy - if a menu item's name
 * or price changes after an order is placed, the order retains the original
 * values at the time of purchase.
 *
 * The [menuItemId] is kept for reference/audit purposes, but the business-relevant
 * data (name, price) is captured at order creation time.
 *
 * This is a deliberate domain design choice, not a relational artifact.
 */
data class OrderItem(
    @field:NotNull(message = "Order item ID cannot be null")
    val id: OrderItemId,

    @field:NotNull(message = "Menu item ID cannot be null")
    val menuItemId: MenuItemId,

    @field:NotBlank(message = "Menu item name cannot be blank")
    @field:Size(min = 1, max = 200, message = "Menu item name must be between 1 and 200 characters")
    val menuItemName: String,

    @field:Positive(message = "Quantity must be positive")
    @field:Max(value = 99, message = "Quantity cannot exceed 99")
    val quantity: Int,

    @field:NotNull(message = "Unit price cannot be null")
    @field:Valid
    val unitPrice: Price,

    @field:Size(max = 500, message = "Notes cannot exceed 500 characters")
    val notes: String = ""
) {
    init {
        require(quantity > 0) { "Order item quantity must be positive" }
        require(menuItemName.isNotBlank()) { "Menu item name cannot be blank" }
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
