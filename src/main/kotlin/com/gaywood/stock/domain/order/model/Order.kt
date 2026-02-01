package com.gaywood.stock.domain.order.model

import com.gaywood.stock.domain.menu.model.Price
import com.gaywood.stock.domain.order.event.OrderCreated
import com.gaywood.stock.domain.order.event.OrderStatusChanged
import com.gaywood.stock.domain.shared.AggregateRoot
import com.gaywood.stock.domain.staff.model.StaffId
import java.time.Instant

class Order private constructor(
    override val id: OrderId,
    private val _items: MutableList<OrderItem>,
    private var _status: OrderStatus,
    val tableNumber: Int?,
    val staffId: StaffId,
    val createdAt: Instant
) : AggregateRoot<OrderId>() {

    val items: List<OrderItem> get() = _items.toList()
    val status: OrderStatus get() = _status

    val totalAmount: Price
        get() = _items.takeIf { it.isNotEmpty() }
            ?.map { it.totalPrice }
            ?.reduce { acc, price -> acc + price }
            ?: Price.zero()

    fun addItem(item: OrderItem) {
        require(_status == OrderStatus.PENDING) { "Cannot add items to a non-pending order" }
        _items.add(item)
    }

    fun removeItem(itemId: OrderItemId) {
        require(_status == OrderStatus.PENDING) { "Cannot remove items from a non-pending order" }
        _items.removeIf { it.id == itemId }
    }

    fun updateStatus(newStatus: OrderStatus) {
        val validTransitions = when (_status) {
            OrderStatus.PENDING -> setOf(OrderStatus.IN_PROGRESS, OrderStatus.CANCELLED)
            OrderStatus.IN_PROGRESS -> setOf(OrderStatus.READY, OrderStatus.CANCELLED)
            OrderStatus.READY -> setOf(OrderStatus.SERVED, OrderStatus.CANCELLED)
            OrderStatus.SERVED -> setOf(OrderStatus.PAID)
            OrderStatus.PAID -> emptySet()
            OrderStatus.CANCELLED -> emptySet()
        }

        require(newStatus in validTransitions) {
            "Invalid status transition from ${_status.displayName} to ${newStatus.displayName}"
        }

        val oldStatus = _status
        _status = newStatus
        registerEvent(OrderStatusChanged(id, oldStatus, newStatus))
    }

    fun cancel() {
        require(_status != OrderStatus.PAID && _status != OrderStatus.CANCELLED) {
            "Cannot cancel a ${_status.displayName.lowercase()} order"
        }
        val oldStatus = _status
        _status = OrderStatus.CANCELLED
        registerEvent(OrderStatusChanged(id, oldStatus, OrderStatus.CANCELLED))
    }

    fun generateBill(): Bill {
        return Bill(
            orderId = id,
            items = _items.map { item ->
                BillLineItem(
                    description = item.menuItemName,
                    quantity = item.quantity,
                    unitPrice = item.unitPrice,
                    totalPrice = item.totalPrice
                )
            },
            totalAmount = totalAmount,
            tableNumber = tableNumber,
            generatedAt = Instant.now()
        )
    }

    companion object {
        fun create(
            id: OrderId = OrderId.generate(),
            items: List<OrderItem> = emptyList(),
            tableNumber: Int? = null,
            staffId: StaffId,
            createdAt: Instant = Instant.now()
        ): Order {
            val order = Order(
                id = id,
                _items = items.toMutableList(),
                _status = OrderStatus.PENDING,
                tableNumber = tableNumber,
                staffId = staffId,
                createdAt = createdAt
            )
            order.registerEvent(OrderCreated(id, tableNumber, staffId, items))
            return order
        }

        fun reconstitute(
            id: OrderId,
            items: List<OrderItem>,
            status: OrderStatus,
            tableNumber: Int?,
            staffId: StaffId,
            createdAt: Instant
        ): Order = Order(
            id = id,
            _items = items.toMutableList(),
            _status = status,
            tableNumber = tableNumber,
            staffId = staffId,
            createdAt = createdAt
        )
    }
}
