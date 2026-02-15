package com.gaywood.stock.fixtures

import com.gaywood.stock.domain.menu.model.MenuItemId
import com.gaywood.stock.domain.menu.model.Price
import com.gaywood.stock.domain.order.model.Order
import com.gaywood.stock.domain.order.model.OrderId
import com.gaywood.stock.domain.order.model.OrderItem
import com.gaywood.stock.domain.order.model.OrderItemId
import com.gaywood.stock.domain.staff.model.StaffId
import java.math.BigDecimal

object OrderFixtures {

    fun ginOrderItem(
        id: OrderItemId = OrderItemId.generate(),
        menuItemId: MenuItemId = MenuItemId.generate(),
        quantity: Int = 1,
        price: Price = Price(BigDecimal("3.50"))
    ): OrderItem = OrderItem.create(
        id = id,
        menuItemId = menuItemId,
        menuItemName = "Gin",
        quantity = quantity,
        unitPrice = price
    )

    fun tonicOrderItem(
        id: OrderItemId = OrderItemId.generate(),
        menuItemId: MenuItemId = MenuItemId.generate(),
        quantity: Int = 1,
        price: Price = Price(BigDecimal("1.00"))
    ): OrderItem = OrderItem.create(
        id = id,
        menuItemId = menuItemId,
        menuItemName = "Tonic",
        quantity = quantity,
        unitPrice = price
    )

    fun fishAndChipsOrderItem(
        id: OrderItemId = OrderItemId.generate(),
        menuItemId: MenuItemId = MenuItemId.generate(),
        quantity: Int = 1,
        price: Price = Price(BigDecimal("8.50"))
    ): OrderItem = OrderItem.create(
        id = id,
        menuItemId = menuItemId,
        menuItemName = "Fish and Chips",
        quantity = quantity,
        unitPrice = price
    )

    fun sausageMashOrderItem(
        id: OrderItemId = OrderItemId.generate(),
        menuItemId: MenuItemId = MenuItemId.generate(),
        quantity: Int = 1,
        price: Price = Price(BigDecimal("8.00"))
    ): OrderItem = OrderItem.create(
        id = id,
        menuItemId = menuItemId,
        menuItemName = "Sausage and Mash",
        quantity = quantity,
        unitPrice = price
    )

    fun pendingOrder(
        id: OrderId = OrderId.generate(),
        staffId: StaffId = StaffId.generate(),
        tableNumber: Int? = 5,
        items: List<OrderItem> = emptyList()
    ): Order = Order.create(
        id = id,
        items = items,
        tableNumber = tableNumber,
        staffId = staffId
    )

    fun twoGinAndTonicsWithFishAndChips(
        staffId: StaffId = StaffId.generate(),
        ginMenuItemId: MenuItemId = MenuItemId.generate(),
        tonicMenuItemId: MenuItemId = MenuItemId.generate(),
        fishAndChipsMenuItemId: MenuItemId = MenuItemId.generate(),
        tableNumber: Int = 5
    ): Order {
        val items = listOf(
            ginOrderItem(menuItemId = ginMenuItemId, quantity = 2),
            tonicOrderItem(menuItemId = tonicMenuItemId, quantity = 2),
            fishAndChipsOrderItem(menuItemId = fishAndChipsMenuItemId, quantity = 2)
        )
        return pendingOrder(staffId = staffId, tableNumber = tableNumber, items = items)
    }
}
