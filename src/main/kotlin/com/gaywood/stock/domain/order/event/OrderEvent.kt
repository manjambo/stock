package com.gaywood.stock.domain.order.event

import com.gaywood.stock.domain.order.model.OrderId
import com.gaywood.stock.domain.order.model.OrderItem
import com.gaywood.stock.domain.order.model.OrderStatus
import com.gaywood.stock.domain.shared.DomainEvent
import com.gaywood.stock.domain.staff.model.StaffId
import java.time.Instant

sealed class OrderEvent : DomainEvent {
    abstract val orderId: OrderId
    abstract override val occurredAt: Instant
}

data class OrderCreated(
    override val orderId: OrderId,
    val tableNumber: Int?,
    val staffId: StaffId,
    val items: List<OrderItem>,
    override val occurredAt: Instant = Instant.now()
) : OrderEvent()

data class OrderStatusChanged(
    override val orderId: OrderId,
    val previousStatus: OrderStatus,
    val newStatus: OrderStatus,
    override val occurredAt: Instant = Instant.now()
) : OrderEvent()
