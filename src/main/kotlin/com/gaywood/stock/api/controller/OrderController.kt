package com.gaywood.stock.api.controller

import com.gaywood.stock.api.dto.*
import com.gaywood.stock.application.OrderService
import com.gaywood.stock.domain.order.model.OrderStatus
import jakarta.validation.Valid
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/orders")
class OrderController(
    private val orderService: OrderService
) {

    @PostMapping
    fun createOrder(@RequestBody @Valid request: CreateOrderRequest): ResponseEntity<OrderResponse> = runBlocking {
        val order = orderService.placeOrder(
            staffId = request.staffId,
            tableNumber = request.tableNumber,
            items = request.items.map { it.toServiceInput() }
        )
        ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.from(order))
    }

    private fun OrderItemRequest.toServiceInput() = OrderService.OrderItemInput(
        menuItemId = menuItemId,
        quantity = quantity,
        notes = notes
    )

    @GetMapping
    fun getActiveOrders(): List<OrderResponse> = runBlocking {
        orderService.getActiveOrders().map { OrderResponse.from(it) }
    }

    @GetMapping("/{orderId}")
    fun getOrder(@PathVariable orderId: String): OrderResponse = runBlocking {
        val order = orderService.getOrder(orderId)
            ?: throw OrderNotFoundException(orderId)
        OrderResponse.from(order)
    }

    @GetMapping("/{orderId}/bill")
    fun getBill(@PathVariable orderId: String): BillResponse = runBlocking {
        val bill = orderService.getBill(orderId)
        BillResponse.from(bill)
    }

    @PostMapping("/{orderId}/status")
    fun updateStatus(
        @PathVariable orderId: String,
        @RequestParam status: String
    ): OrderResponse = runBlocking {
        val newStatus = OrderStatus.valueOf(status.uppercase())
        val order = orderService.updateOrderStatus(orderId, newStatus)
        OrderResponse.from(order)
    }

    @PostMapping("/{orderId}/cancel")
    fun cancelOrder(@PathVariable orderId: String): OrderResponse = runBlocking {
        val order = orderService.cancelOrder(orderId)
        OrderResponse.from(order)
    }

    @GetMapping("/table/{tableNumber}")
    fun getOrdersByTable(@PathVariable tableNumber: Int): List<OrderResponse> = runBlocking {
        orderService.getOrdersByTable(tableNumber).map { OrderResponse.from(it) }
    }
}

class OrderNotFoundException(orderId: String) : RuntimeException("Order not found: $orderId")
