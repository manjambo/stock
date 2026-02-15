package com.gaywood.stock.api.controller

import com.gaywood.stock.api.dto.*
import com.gaywood.stock.application.OrderService
import com.gaywood.stock.domain.order.model.OrderStatus
import com.gaywood.stock.domain.shared.OrderNotFoundException
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/orders")
@Tag(name = "Orders", description = "Order management endpoints")
class OrderController(
    private val orderService: OrderService
) {

    companion object {
        private const val DEFAULT_PAGE_SIZE = 20
        private const val MAX_PAGE_SIZE = 100
        private val VALID_STATUSES = OrderStatus.entries.map { it.name }.toSet()
    }

    @PostMapping
    @Operation(summary = "Create a new order", description = "Creates a new order with the specified items for a table")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Order created successfully",
            content = [Content(schema = Schema(implementation = OrderResponse::class))]),
        ApiResponse(responseCode = "400", description = "Invalid request",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]),
        ApiResponse(responseCode = "404", description = "Menu item not found",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))])
    ])
    fun createOrder(@RequestBody @Valid request: CreateOrderRequest): ResponseEntity<OrderResponse> {
        val order = orderService.placeOrder(
            staffId = request.staffId,
            tableNumber = request.tableNumber,
            items = request.items.map { it.toServiceInput() }
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.from(order))
    }

    private fun OrderItemRequest.toServiceInput() = OrderService.OrderItemInput(
        menuItemId = menuItemId,
        quantity = quantity,
        notes = notes
    )

    @GetMapping
    @Operation(summary = "Get active orders", description = "Retrieves orders that are not yet completed or cancelled, with pagination")
    @ApiResponse(responseCode = "200", description = "Page of active orders")
    fun getActiveOrders(
        @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "Page size (max 100)") @RequestParam(defaultValue = "20") size: Int
    ): Page<OrderResponse> {
        val pageable = PageRequest.of(
            page.coerceAtLeast(0),
            size.coerceIn(1, MAX_PAGE_SIZE),
            Sort.by(Sort.Direction.DESC, "createdAt")
        )
        return orderService.getActiveOrders(pageable).map { OrderResponse.from(it) }
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID", description = "Retrieves a specific order by its ID")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Order found",
            content = [Content(schema = Schema(implementation = OrderResponse::class))]),
        ApiResponse(responseCode = "404", description = "Order not found",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))])
    ])
    fun getOrder(
        @Parameter(description = "Order ID (UUID format)") @PathVariable orderId: String
    ): OrderResponse {
        val order = orderService.getOrder(orderId)
            ?: throw OrderNotFoundException(orderId)
        return OrderResponse.from(order)
    }

    @GetMapping("/{orderId}/bill")
    @Operation(summary = "Get bill for order", description = "Generates and retrieves the bill for a specific order")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Bill generated",
            content = [Content(schema = Schema(implementation = BillResponse::class))]),
        ApiResponse(responseCode = "404", description = "Order not found",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))])
    ])
    fun getBill(
        @Parameter(description = "Order ID (UUID format)") @PathVariable orderId: String
    ): BillResponse {
        val bill = orderService.getBill(orderId)
        return BillResponse.from(bill)
    }

    @PostMapping("/{orderId}/status")
    @Operation(summary = "Update order status", description = "Updates the status of an order (PENDING -> IN_PROGRESS -> READY -> SERVED -> PAID)")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Status updated",
            content = [Content(schema = Schema(implementation = OrderResponse::class))]),
        ApiResponse(responseCode = "400", description = "Invalid status transition",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]),
        ApiResponse(responseCode = "404", description = "Order not found",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))])
    ])
    fun updateStatus(
        @Parameter(description = "Order ID (UUID format)") @PathVariable orderId: String,
        @Parameter(description = "New status (PENDING, IN_PROGRESS, READY, SERVED, PAID, CANCELLED)") @RequestParam status: String
    ): OrderResponse {
        val normalizedStatus = status.uppercase()
        require(normalizedStatus in VALID_STATUSES) {
            "Invalid status '$status'. Valid values are: ${VALID_STATUSES.joinToString()}"
        }
        val newStatus = OrderStatus.valueOf(normalizedStatus)
        val order = orderService.updateOrderStatus(orderId, newStatus)
        return OrderResponse.from(order)
    }

    @PostMapping("/{orderId}/cancel")
    @Operation(summary = "Cancel order", description = "Cancels an order that is not yet paid or already cancelled")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Order cancelled",
            content = [Content(schema = Schema(implementation = OrderResponse::class))]),
        ApiResponse(responseCode = "400", description = "Cannot cancel order in current state",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]),
        ApiResponse(responseCode = "404", description = "Order not found",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))])
    ])
    fun cancelOrder(
        @Parameter(description = "Order ID (UUID format)") @PathVariable orderId: String
    ): OrderResponse {
        val order = orderService.cancelOrder(orderId)
        return OrderResponse.from(order)
    }

    @GetMapping("/table/{tableNumber}")
    @Operation(summary = "Get orders by table", description = "Retrieves orders for a specific table number, with pagination")
    @ApiResponse(responseCode = "200", description = "Page of orders for the table")
    fun getOrdersByTable(
        @Parameter(description = "Table number (1-999)") @PathVariable tableNumber: Int,
        @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "Page size (max 100)") @RequestParam(defaultValue = "20") size: Int
    ): Page<OrderResponse> {
        val pageable = PageRequest.of(
            page.coerceAtLeast(0),
            size.coerceIn(1, MAX_PAGE_SIZE),
            Sort.by(Sort.Direction.DESC, "createdAt")
        )
        return orderService.getOrdersByTable(tableNumber, pageable).map { OrderResponse.from(it) }
    }
}
