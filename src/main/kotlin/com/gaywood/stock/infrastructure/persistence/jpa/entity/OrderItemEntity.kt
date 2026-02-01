package com.gaywood.stock.infrastructure.persistence.jpa.entity

import com.gaywood.stock.domain.menu.model.MenuItemId
import com.gaywood.stock.domain.menu.model.Price
import com.gaywood.stock.domain.order.model.OrderItem
import com.gaywood.stock.domain.order.model.OrderItemId
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant
import java.util.Currency

@Entity
@Table(name = "order_items")
class OrderItemEntity(
    @Id
    @Column(name = "id", length = 36)
    var id: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    var order: OrderEntity? = null,

    @Column(name = "menu_item_id", length = 36, nullable = false)
    var menuItemId: String = "",

    @Column(name = "menu_item_name", length = 255, nullable = false)
    var menuItemName: String = "",

    @Column(name = "quantity", nullable = false)
    var quantity: Int = 0,

    @Column(name = "unit_price_amount", precision = 19, scale = 4, nullable = false)
    var unitPriceAmount: BigDecimal = BigDecimal.ZERO,

    @Column(name = "unit_price_currency", length = 3, nullable = false)
    var unitPriceCurrency: String = "GBP",

    @Column(name = "notes", columnDefinition = "TEXT")
    var notes: String = "",

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now()
) {
    fun toDomain(): OrderItem {
        return OrderItem(
            id = OrderItemId(id),
            menuItemId = MenuItemId(menuItemId),
            menuItemName = menuItemName,
            quantity = quantity,
            unitPrice = Price(unitPriceAmount, Currency.getInstance(unitPriceCurrency)),
            notes = notes
        )
    }

    companion object {
        fun from(item: OrderItem, orderEntity: OrderEntity): OrderItemEntity {
            return OrderItemEntity(
                id = item.id.value,
                order = orderEntity,
                menuItemId = item.menuItemId.value,
                menuItemName = item.menuItemName,
                quantity = item.quantity,
                unitPriceAmount = item.unitPrice.amount,
                unitPriceCurrency = item.unitPrice.currency.currencyCode,
                notes = item.notes,
                createdAt = Instant.now()
            )
        }
    }
}
