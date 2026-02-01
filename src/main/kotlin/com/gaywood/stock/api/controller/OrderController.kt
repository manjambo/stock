package com.gaywood.stock.api.controller

import com.gaywood.stock.api.dto.*
import com.gaywood.stock.application.OrderService
import com.gaywood.stock.domain.order.model.OrderStatus
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/orders")
class OrderController(
    private val orderService: OrderService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createOrder(@RequestBody @Valid request: CreateOrderRequest): OrderResponse {
        val order = orderService.placeOrder(
            staffId = request.staffId,
            tableNumber = request.tableNumber,
            items = request.items.map { it.toServiceInput() }
        )
        return OrderResponse.from(order)
    }

    private fun OrderItemRequest.toServiceInput() = OrderService.OrderItemInput(
        menuItemId = menuItemId,
        quantity = quantity,
        notes = notes
    )

    @GetMapping
    fun getActiveOrders(): List<OrderResponse> {
        return orderService.getActiveOrders().map { OrderResponse.from(it) }
    }

    @GetMapping("/{orderId}")
    fun getOrder(@PathVariable orderId: String): OrderResponse {
        val order = orderService.getOrder(orderId)
            ?: throw OrderNotFoundException(orderId)
        return OrderResponse.from(order)
    }

    @GetMapping("/{orderId}/bill")
    fun getBill(@PathVariable orderId: String): BillResponse {
        val bill = orderService.getBill(orderId)
        return BillResponse.from(bill)
    }

    @PostMapping("/{orderId}/status")
    fun updateStatus(
        @PathVariable orderId: String,
        @RequestParam status: String
    ): OrderResponse {
        val newStatus = OrderStatus.valueOf(status.uppercase())
        val order = orderService.updateOrderStatus(orderId, newStatus)
        return OrderResponse.from(order)
    }

    @PostMapping("/{orderId}/cancel")
    fun cancelOrder(@PathVariable orderId: String): OrderResponse {
        val order = orderService.cancelOrder(orderId)
        return OrderResponse.from(order)
    }

    @GetMapping("/table/{tableNumber}")
    fun getOrdersByTable(@PathVariable tableNumber: Int): List<OrderResponse> {
        return orderService.getOrdersByTable(tableNumber).map { OrderResponse.from(it) }
    }
}

class OrderNotFoundException(orderId: String) : RuntimeException("Order not found: $orderId")
