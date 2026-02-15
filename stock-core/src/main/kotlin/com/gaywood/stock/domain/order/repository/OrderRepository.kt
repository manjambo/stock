package com.gaywood.stock.domain.order.repository

import com.gaywood.stock.domain.order.model.Order
import com.gaywood.stock.domain.order.model.OrderId
import com.gaywood.stock.domain.order.model.OrderStatus
import com.gaywood.stock.domain.staff.model.StaffId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface OrderRepository {
    fun save(order: Order): Order
    fun findById(id: OrderId): Order?
    fun findByStatus(status: OrderStatus): List<Order>
    fun findByStaffId(staffId: StaffId): List<Order>
    fun findByTableNumber(tableNumber: Int): List<Order>
    fun findByTableNumber(tableNumber: Int, pageable: Pageable): Page<Order>
    fun findActiveOrders(): List<Order>
    fun findActiveOrders(pageable: Pageable): Page<Order>
    fun findAll(): List<Order>
    fun delete(id: OrderId)
}
