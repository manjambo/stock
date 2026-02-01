package com.gaywood.stock.api.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Positive

data class CreateOrderRequest(
    val tableNumber: Int? = null,

    @field:NotBlank(message = "Staff ID is required")
    val staffId: String,

    @field:NotEmpty(message = "At least one item is required")
    @field:Valid
    val items: List<OrderItemRequest>
)

data class OrderItemRequest(
    @field:NotBlank(message = "Menu item ID is required")
    val menuItemId: String,

    @field:Positive(message = "Quantity must be positive")
    val quantity: Int,

    val notes: String = ""
)

data class OrderResponse(
    val id: String,
    val status: String,
    val tableNumber: Int?,
    val staffId: String,
    val items: List<OrderItemResponse>,
    val totalAmount: String,
    val createdAt: String
) {
    companion object {
        fun from(order: com.gaywood.stock.domain.order.model.Order) = OrderResponse(
            id = order.id.value,
            status = order.status.name,
            tableNumber = order.tableNumber,
            staffId = order.staffId.value,
            items = order.items.map { OrderItemResponse.from(it) },
            totalAmount = order.totalAmount.toString(),
            createdAt = order.createdAt.toString()
        )
    }
}

data class OrderItemResponse(
    val id: String,
    val menuItemId: String,
    val menuItemName: String,
    val quantity: Int,
    val unitPrice: String,
    val totalPrice: String,
    val notes: String
) {
    companion object {
        fun from(item: com.gaywood.stock.domain.order.model.OrderItem) = OrderItemResponse(
            id = item.id.value,
            menuItemId = item.menuItemId.value,
            menuItemName = item.menuItemName,
            quantity = item.quantity,
            unitPrice = item.unitPrice.toString(),
            totalPrice = item.totalPrice.toString(),
            notes = item.notes
        )
    }
}

data class BillResponse(
    val orderId: String,
    val items: List<BillLineItemResponse>,
    val totalAmount: String,
    val tableNumber: Int?,
    val generatedAt: String,
    val formattedBill: String
) {
    companion object {
        fun from(bill: com.gaywood.stock.domain.order.model.Bill) = BillResponse(
            orderId = bill.orderId.value,
            items = bill.items.map { BillLineItemResponse.from(it) },
            totalAmount = bill.totalAmount.toString(),
            tableNumber = bill.tableNumber,
            generatedAt = bill.generatedAt.toString(),
            formattedBill = bill.formatAsText()
        )
    }
}

data class BillLineItemResponse(
    val description: String,
    val quantity: Int,
    val unitPrice: String,
    val totalPrice: String
) {
    companion object {
        fun from(item: com.gaywood.stock.domain.order.model.BillLineItem) = BillLineItemResponse(
            description = item.description,
            quantity = item.quantity,
            unitPrice = item.unitPrice.toString(),
            totalPrice = item.totalPrice.toString()
        )
    }
}

data class ErrorResponse(
    val error: String,
    val message: String
)
