package com.gaywood.stock.application

import com.gaywood.stock.domain.menu.model.MenuItemId
import com.gaywood.stock.domain.menu.repository.MenuRepository
import com.gaywood.stock.domain.order.model.*
import com.gaywood.stock.domain.order.repository.OrderRepository
import com.gaywood.stock.domain.staff.model.StaffId
import com.gaywood.stock.domain.staff.repository.StaffRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    suspend fun placeOrder(
        staffId: String,
        tableNumber: Int?,
        items: List<OrderItemInput>
    ): Order {
        val staff = findStaffOrThrow(staffId)
        val allMenuItems = fetchAllMenuItems()

        val orderItems = items.map { toOrderItem(it, allMenuItems) }
        val order = Order.create(
            items = orderItems,
            tableNumber = tableNumber,
            staffId = staff.id
        )
        return saveOrder(order)
    }

    private suspend fun findStaffOrThrow(staffId: String) = withContext(Dispatchers.IO) {
        staffRepository.findById(StaffId(staffId))
            ?: throw IllegalArgumentException("Staff member not found: $staffId")
    }

    private suspend fun fetchAllMenuItems() = withContext(Dispatchers.IO) {
        menuRepository.findAll().flatMap { it.items }
    }

    private fun toOrderItem(
        input: OrderItemInput,
        allMenuItems: List<com.gaywood.stock.domain.menu.model.MenuItem>
    ): OrderItem {
        val menuItem = allMenuItems.find { it.id == MenuItemId(input.menuItemId) }
            ?: throw IllegalArgumentException("Menu item not found: ${input.menuItemId}")
        check(menuItem.available) { "Menu item is not available: ${menuItem.name}" }
        return OrderItem.create(
            menuItemId = menuItem.id,
            menuItemName = menuItem.name,
            quantity = input.quantity,
            unitPrice = menuItem.price,
            notes = input.notes
        )
    }

    private suspend fun saveOrder(order: Order) = withContext(Dispatchers.IO) {
        orderRepository.save(order)
    }

    @Transactional(readOnly = true)
    suspend fun getOrder(orderId: String): Order? = withContext(Dispatchers.IO) {
        orderRepository.findById(OrderId(orderId))
    }

    @Transactional(readOnly = true)
    suspend fun getBill(orderId: String): Bill = withContext(Dispatchers.IO) {
        val order = orderRepository.findById(OrderId(orderId))
            ?: throw IllegalArgumentException("Order not found: $orderId")
        order.generateBill()
    }

    suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus): Order = withContext(Dispatchers.IO) {
        val order = orderRepository.findById(OrderId(orderId))
            ?: throw IllegalArgumentException("Order not found: $orderId")

        order.updateStatus(newStatus)
        orderRepository.save(order)
    }

    suspend fun cancelOrder(orderId: String): Order = withContext(Dispatchers.IO) {
        val order = orderRepository.findById(OrderId(orderId))
            ?: throw IllegalArgumentException("Order not found: $orderId")

        order.cancel()
        orderRepository.save(order)
    }

    @Transactional(readOnly = true)
    suspend fun getActiveOrders(): List<Order> = withContext(Dispatchers.IO) {
        orderRepository.findActiveOrders()
    }

    @Transactional(readOnly = true)
    suspend fun getOrdersByTable(tableNumber: Int): List<Order> = withContext(Dispatchers.IO) {
        orderRepository.findByTableNumber(tableNumber)
    }
}
