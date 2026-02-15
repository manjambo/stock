package com.gaywood.stock.application

import com.gaywood.stock.domain.menu.model.MenuItemId
import com.gaywood.stock.domain.menu.repository.MenuRepository
import com.gaywood.stock.domain.order.model.*
import com.gaywood.stock.domain.order.repository.OrderRepository
import com.gaywood.stock.domain.shared.MenuItemNotFoundException
import com.gaywood.stock.domain.shared.OrderNotFoundException
import com.gaywood.stock.domain.shared.StaffNotFoundException
import com.gaywood.stock.domain.staff.model.StaffId
import com.gaywood.stock.domain.staff.repository.StaffRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val menuRepository: MenuRepository,
    private val staffRepository: StaffRepository
) {

    data class OrderItemInput(
        val menuItemId: String,
        val quantity: Int,
        val notes: String = ""
    )

    @Transactional
    fun placeOrder(
        staffId: String,
        tableNumber: Int?,
        items: List<OrderItemInput>
    ): Order {
        val staff = staffRepository.findById(StaffId(staffId))
            ?: throw StaffNotFoundException(staffId)
        val allMenuItems = menuRepository.findAll().flatMap { it.items }

        val orderItems = items.map { toOrderItem(it, allMenuItems) }
        val order = Order.create(
            items = orderItems,
            tableNumber = tableNumber,
            staffId = staff.id
        )
        return orderRepository.save(order)
    }

    private fun toOrderItem(
        input: OrderItemInput,
        allMenuItems: List<com.gaywood.stock.domain.menu.model.MenuItem>
    ): OrderItem {
        val menuItem = allMenuItems.find { it.id == MenuItemId(input.menuItemId) }
            ?: throw MenuItemNotFoundException(input.menuItemId)
        require(menuItem.available) { "Menu item is not available: ${menuItem.name}" }
        return OrderItem.create(
            menuItemId = menuItem.id,
            menuItemName = menuItem.name,
            quantity = input.quantity,
            unitPrice = menuItem.price,
            notes = input.notes
        )
    }

    @Transactional(readOnly = true)
    fun getOrder(orderId: String): Order? {
        return orderRepository.findById(OrderId(orderId))
    }

    @Transactional(readOnly = true)
    fun getBill(orderId: String): Bill {
        val order = orderRepository.findById(OrderId(orderId))
            ?: throw OrderNotFoundException(orderId)
        return order.generateBill()
    }

    @Transactional
    fun updateOrderStatus(orderId: String, newStatus: OrderStatus): Order {
        val order = orderRepository.findById(OrderId(orderId))
            ?: throw OrderNotFoundException(orderId)

        order.updateStatus(newStatus)
        return orderRepository.save(order)
    }

    @Transactional
    fun cancelOrder(orderId: String): Order {
        val order = orderRepository.findById(OrderId(orderId))
            ?: throw OrderNotFoundException(orderId)

        order.cancel()
        return orderRepository.save(order)
    }

    @Transactional(readOnly = true)
    fun getActiveOrders(): List<Order> {
        return orderRepository.findActiveOrders()
    }

    @Transactional(readOnly = true)
    fun getActiveOrders(pageable: Pageable): Page<Order> {
        return orderRepository.findActiveOrders(pageable)
    }

    @Transactional(readOnly = true)
    fun getOrdersByTable(tableNumber: Int): List<Order> {
        return orderRepository.findByTableNumber(tableNumber)
    }

    @Transactional(readOnly = true)
    fun getOrdersByTable(tableNumber: Int, pageable: Pageable): Page<Order> {
        return orderRepository.findByTableNumber(tableNumber, pageable)
    }
}
