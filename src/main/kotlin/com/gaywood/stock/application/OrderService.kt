package com.gaywood.stock.application

import com.gaywood.stock.domain.menu.model.MenuItemId
import com.gaywood.stock.domain.menu.repository.MenuRepository
import com.gaywood.stock.domain.order.model.*
import com.gaywood.stock.domain.order.repository.OrderRepository
import com.gaywood.stock.domain.staff.model.StaffId
import com.gaywood.stock.domain.staff.repository.StaffRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
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

    fun placeOrder(
        staffId: String,
        tableNumber: Int?,
        items: List<OrderItemInput>
    ): Order {
        val staff = findStaffOrThrow(staffId)
        val orderItems = items.map { toOrderItem(it) }
        val order = Order.create(
            items = orderItems,
            tableNumber = tableNumber,
            staffId = staff.id
        )
        return orderRepository.save(order)
    }

    private fun findStaffOrThrow(staffId: String) =
        staffRepository.findById(StaffId(staffId))
            ?: throw IllegalArgumentException("Staff member not found: $staffId")

    private fun toOrderItem(input: OrderItemInput): OrderItem {
        val menuItem = findMenuItemOrThrow(input.menuItemId)
        requireMenuItemAvailable(menuItem)
        return OrderItem.create(
            menuItemId = menuItem.id,
            menuItemName = menuItem.name,
            quantity = input.quantity,
            unitPrice = menuItem.price,
            notes = input.notes
        )
    }

    private fun findMenuItemOrThrow(menuItemId: String) =
        menuRepository.findAll()
            .flatMap { it.items }
            .find { it.id == MenuItemId(menuItemId) }
            ?: throw IllegalArgumentException("Menu item not found: $menuItemId")

    private fun requireMenuItemAvailable(menuItem: com.gaywood.stock.domain.menu.model.MenuItem) {
        check(menuItem.available) { "Menu item is not available: ${menuItem.name}" }
    }

    @Transactional(readOnly = true)
    fun getOrder(orderId: String): Order? {
        return orderRepository.findById(OrderId(orderId))
    }

    @Transactional(readOnly = true)
    fun getBill(orderId: String): Bill {
        val order = orderRepository.findById(OrderId(orderId))
            ?: throw IllegalArgumentException("Order not found: $orderId")
        return order.generateBill()
    }

    fun updateOrderStatus(orderId: String, newStatus: OrderStatus): Order {
        val order = orderRepository.findById(OrderId(orderId))
            ?: throw IllegalArgumentException("Order not found: $orderId")

        order.updateStatus(newStatus)
        return orderRepository.save(order)
    }

    fun cancelOrder(orderId: String): Order {
        val order = orderRepository.findById(OrderId(orderId))
            ?: throw IllegalArgumentException("Order not found: $orderId")

        order.cancel()
        return orderRepository.save(order)
    }

    @Transactional(readOnly = true)
    fun getActiveOrders(): List<Order> {
        return orderRepository.findActiveOrders()
    }

    @Transactional(readOnly = true)
    fun getOrdersByTable(tableNumber: Int): List<Order> {
        return orderRepository.findByTableNumber(tableNumber)
    }
}
