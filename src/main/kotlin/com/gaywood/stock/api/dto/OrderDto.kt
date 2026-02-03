package com.gaywood.stock.api.dto

import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

private const val UUID_PATTERN = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"

@Schema(description = "Request to create a new order")
data class CreateOrderRequest(
    @Schema(
        description = "Table number (1-999)",
        example = "12",
        nullable = true,
        minimum = "1",
        maximum = "999"
    )
    @field:Min(value = 1, message = "Table number must be at least 1")
    @field:Max(value = 999, message = "Table number cannot exceed 999")
    val tableNumber: Int? = null,

    @Schema(
        description = "ID of the staff member creating the order",
        example = "550e8400-e29b-41d4-a716-446655440000",
        pattern = UUID_PATTERN,
        maxLength = 36
    )
    @field:NotBlank(message = "Staff ID is required")
    @field:Size(max = 36, message = "Staff ID cannot exceed 36 characters")
    @field:Pattern(
        regexp = UUID_PATTERN,
        message = "Staff ID must be a valid UUID"
    )
    val staffId: String,

    @ArraySchema(
        schema = Schema(implementation = OrderItemRequest::class),
        maxItems = 50,
        minItems = 1
    )
    @Schema(description = "List of items in the order")
    @field:NotEmpty(message = "At least one item is required")
    @field:Size(max = 50, message = "Cannot have more than 50 items per order")
    @field:Valid
    val items: List<OrderItemRequest>
)

@Schema(description = "Order item in a create order request")
data class OrderItemRequest(
    @Schema(
        description = "ID of the menu item to order",
        example = "550e8400-e29b-41d4-a716-446655440001",
        pattern = UUID_PATTERN,
        maxLength = 36
    )
    @field:NotBlank(message = "Menu item ID is required")
    @field:Size(max = 36, message = "Menu item ID cannot exceed 36 characters")
    @field:Pattern(
        regexp = UUID_PATTERN,
        message = "Menu item ID must be a valid UUID"
    )
    val menuItemId: String,

    @Schema(description = "Quantity to order (1-99)", example = "2", minimum = "1", maximum = "99")
    @field:Min(value = 1, message = "Quantity must be at least 1")
    @field:Positive(message = "Quantity must be positive")
    @field:Max(value = 99, message = "Quantity cannot exceed 99")
    val quantity: Int,

    @Schema(
        description = "Special notes or modifications",
        example = "No onions please",
        maxLength = 500,
        pattern = "^[\\w\\s.,!?'\\-]*$"
    )
    @field:Size(max = 500, message = "Notes cannot exceed 500 characters")
    val notes: String = ""
)

@Schema(description = "Order details response")
data class OrderResponse(
    @Schema(
        description = "Unique order ID",
        example = "550e8400-e29b-41d4-a716-446655440002",
        pattern = UUID_PATTERN,
        maxLength = 36
    )
    val id: String,
    @Schema(
        description = "Current order status",
        example = "PENDING",
        allowableValues = ["PENDING", "IN_PROGRESS", "READY", "SERVED", "PAID", "CANCELLED"],
        maxLength = 20
    )
    val status: String,
    @Schema(
        description = "Table number (if applicable)",
        example = "12",
        nullable = true,
        minimum = "1",
        maximum = "999"
    )
    val tableNumber: Int?,
    @Schema(
        description = "ID of the staff member who created the order",
        example = "550e8400-e29b-41d4-a716-446655440000",
        pattern = UUID_PATTERN,
        maxLength = 36
    )
    val staffId: String,
    @ArraySchema(
        schema = Schema(implementation = OrderItemResponse::class),
        maxItems = 50
    )
    @Schema(description = "List of items in the order")
    val items: List<OrderItemResponse>,
    @Schema(
        description = "Total order amount with currency",
        example = "£25.50",
        maxLength = 50,
        pattern = "^[£$€]?[0-9]+\\.?[0-9]{0,2}$"
    )
    val totalAmount: String,
    @Schema(
        description = "Timestamp when the order was created",
        example = "2026-02-03T10:30:00Z",
        format = "date-time",
        maxLength = 30
    )
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

@Schema(description = "Order item details in response")
data class OrderItemResponse(
    @Schema(
        description = "Unique order item ID",
        example = "550e8400-e29b-41d4-a716-446655440003",
        pattern = UUID_PATTERN,
        maxLength = 36
    )
    val id: String,
    @Schema(
        description = "Menu item ID",
        example = "550e8400-e29b-41d4-a716-446655440001",
        pattern = UUID_PATTERN,
        maxLength = 36
    )
    val menuItemId: String,
    @Schema(
        description = "Name of the menu item",
        example = "Fish and Chips",
        maxLength = 200,
        pattern = "^[\\w\\s&'\\-]+$"
    )
    val menuItemName: String,
    @Schema(description = "Quantity ordered", example = "2", minimum = "1", maximum = "99")
    val quantity: Int,
    @Schema(
        description = "Price per unit with currency",
        example = "£12.50",
        maxLength = 50,
        pattern = "^[£$€]?[0-9]+\\.?[0-9]{0,2}$"
    )
    val unitPrice: String,
    @Schema(
        description = "Total price for this line with currency",
        example = "£25.00",
        maxLength = 50,
        pattern = "^[£$€]?[0-9]+\\.?[0-9]{0,2}$"
    )
    val totalPrice: String,
    @Schema(
        description = "Special notes or modifications",
        example = "No onions please",
        maxLength = 500,
        pattern = "^[\\w\\s.,!?'\\-]*$"
    )
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

@Schema(description = "Bill/receipt for an order")
data class BillResponse(
    @Schema(
        description = "Order ID this bill belongs to",
        example = "550e8400-e29b-41d4-a716-446655440002",
        pattern = UUID_PATTERN,
        maxLength = 36
    )
    val orderId: String,
    @ArraySchema(
        schema = Schema(implementation = BillLineItemResponse::class),
        maxItems = 50
    )
    @Schema(description = "Line items on the bill")
    val items: List<BillLineItemResponse>,
    @Schema(
        description = "Total amount due with currency",
        example = "£47.50",
        maxLength = 50,
        pattern = "^[£$€]?[0-9]+\\.?[0-9]{0,2}$"
    )
    val totalAmount: String,
    @Schema(
        description = "Table number (if applicable)",
        example = "12",
        nullable = true,
        minimum = "1",
        maximum = "999"
    )
    val tableNumber: Int?,
    @Schema(
        description = "Timestamp when the bill was generated",
        example = "2026-02-03T11:00:00Z",
        format = "date-time",
        maxLength = 30
    )
    val generatedAt: String,
    @Schema(
        description = "Formatted text version of the bill for printing",
        maxLength = 5000
    )
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

@Schema(description = "Line item on a bill")
data class BillLineItemResponse(
    @Schema(
        description = "Item description",
        example = "Fish and Chips",
        maxLength = 200,
        pattern = "^[\\w\\s&'\\-]+$"
    )
    val description: String,
    @Schema(description = "Quantity", example = "2", minimum = "1", maximum = "99")
    val quantity: Int,
    @Schema(
        description = "Price per unit with currency",
        example = "£12.50",
        maxLength = 50,
        pattern = "^[£$€]?[0-9]+\\.?[0-9]{0,2}$"
    )
    val unitPrice: String,
    @Schema(
        description = "Total price for this line with currency",
        example = "£25.00",
        maxLength = 50,
        pattern = "^[£$€]?[0-9]+\\.?[0-9]{0,2}$"
    )
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

@Schema(description = "Error response")
data class ErrorResponse(
    @Schema(
        description = "Error type/code",
        example = "NOT_FOUND",
        maxLength = 50,
        pattern = "^[A-Z_]+$"
    )
    val error: String,
    @Schema(
        description = "Human-readable error message",
        example = "Order not found: 550e8400-e29b-41d4-a716-446655440099",
        maxLength = 500
    )
    val message: String
)
