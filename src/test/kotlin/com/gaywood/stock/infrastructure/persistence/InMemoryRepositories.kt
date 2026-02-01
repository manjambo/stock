package com.gaywood.stock.infrastructure.persistence

import com.gaywood.stock.domain.menu.model.Menu
import com.gaywood.stock.domain.menu.model.MenuId
import com.gaywood.stock.domain.menu.model.MenuType
import com.gaywood.stock.domain.menu.repository.MenuRepository
import com.gaywood.stock.domain.order.model.Order
import com.gaywood.stock.domain.order.model.OrderId
import com.gaywood.stock.domain.order.model.OrderStatus
import com.gaywood.stock.domain.order.repository.OrderRepository
import com.gaywood.stock.domain.staff.model.Staff
import com.gaywood.stock.domain.staff.model.StaffId
import com.gaywood.stock.domain.staff.model.StaffRole
import com.gaywood.stock.domain.staff.repository.StaffRepository
import com.gaywood.stock.domain.stock.model.*
import com.gaywood.stock.domain.stock.repository.StockRepository

class InMemoryStockRepository : StockRepository {
    private val items = mutableMapOf<String, StockItem>()

    override fun save(stockItem: StockItem): StockItem {
        items[stockItem.id.value] = stockItem
        return stockItem
    }

    override fun findById(id: StockItemId): StockItem? {
        return items[id.value]
    }

    override fun findByLocation(location: StockLocation): List<StockItem> {
        return items.values.filter { it.location == location }
    }

    override fun findByCategory(category: StockCategory): List<StockItem> {
        return items.values.filter { it.category == category }
    }

    override fun findByAllergen(allergen: Allergen): List<StockItem> {
        return items.values.filter { it.containsAllergen(allergen) }
    }

    override fun findContainingAnyAllergen(allergens: Set<Allergen>): List<StockItem> {
        return items.values.filter { item ->
            item.allergens.any { it in allergens }
        }
    }

    override fun findLowStockItems(): List<StockItem> {
        return items.values.filter { it.isLowStock() }
    }

    override fun findAll(): List<StockItem> {
        return items.values.toList()
    }

    override fun delete(id: StockItemId) {
        items.remove(id.value)
    }
}

class InMemoryOrderRepository : OrderRepository {
    private val orders = mutableMapOf<String, Order>()

    override fun save(order: Order): Order {
        orders[order.id.value] = order
        return order
    }

    override fun findById(id: OrderId): Order? {
        return orders[id.value]
    }

    override fun findByStatus(status: OrderStatus): List<Order> {
        return orders.values.filter { it.status == status }
    }

    override fun findByStaffId(staffId: com.gaywood.stock.domain.staff.model.StaffId): List<Order> {
        return orders.values.filter { it.staffId == staffId }
    }

    override fun findByTableNumber(tableNumber: Int): List<Order> {
        return orders.values.filter { it.tableNumber == tableNumber }
    }

    override fun findActiveOrders(): List<Order> {
        val activeStatuses = setOf(
            OrderStatus.PENDING,
            OrderStatus.IN_PROGRESS,
            OrderStatus.READY,
            OrderStatus.SERVED
        )
        return orders.values.filter { it.status in activeStatuses }
    }

    override fun findAll(): List<Order> {
        return orders.values.toList()
    }

    override fun delete(id: OrderId) {
        orders.remove(id.value)
    }
}

class InMemoryMenuRepository : MenuRepository {
    private val menus = mutableMapOf<String, Menu>()

    override fun save(menu: Menu): Menu {
        menus[menu.id.value] = menu
        return menu
    }

    override fun findById(id: MenuId): Menu? {
        return menus[id.value]
    }

    override fun findByType(type: MenuType): List<Menu> {
        return menus.values.filter { it.type == type }
    }

    override fun findByName(name: String): Menu? {
        return menus.values.find { it.name == name }
    }

    override fun findActive(): List<Menu> {
        return menus.values.filter { it.active }
    }

    override fun findAll(): List<Menu> {
        return menus.values.toList()
    }

    override fun delete(id: MenuId) {
        menus.remove(id.value)
    }
}

class InMemoryStaffRepository : StaffRepository {
    private val staff = mutableMapOf<String, Staff>()

    override fun save(staff: Staff): Staff {
        this.staff[staff.id.value] = staff
        return staff
    }

    override fun findById(id: StaffId): Staff? {
        return staff[id.value]
    }

    override fun findByRole(roleType: Class<out StaffRole>): List<Staff> {
        return staff.values.filter { roleType.isInstance(it.role) }
    }

    override fun findAll(): List<Staff> {
        return staff.values.toList()
    }

    override fun delete(id: StaffId) {
        staff.remove(id.value)
    }
}
