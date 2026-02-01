package com.gaywood.stock.domain.order.repository

import com.gaywood.stock.domain.order.model.Order
import com.gaywood.stock.domain.order.model.OrderId
import com.gaywood.stock.domain.order.model.OrderStatus
import com.gaywood.stock.domain.staff.model.StaffId

interface OrderRepository {
    fun save(order: Order): Order
    fun findById(id: OrderId): Order?
    fun findByStatus(status: OrderStatus): List<Order>
    fun findByStaffId(staffId: StaffId): List<Order>
    fun findByTableNumber(tableNumber: Int): List<Order>
    fun findActiveOrders(): List<Order>
    fun findAll(): List<Order>
    fun delete(id: OrderId)
}
