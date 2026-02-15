package com.gaywood.stock.infrastructure.persistence.jpa.entity

import com.gaywood.stock.domain.menu.model.MenuItemIngredient
import com.gaywood.stock.domain.stock.model.Quantity
import com.gaywood.stock.domain.stock.model.StockItemId
import com.gaywood.stock.domain.stock.model.Unit
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "menu_item_ingredients")
class MenuItemIngredientEntity(
    @Id
    @Column(name = "id", length = 36)
    var id: String = UUID.randomUUID().toString(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    var menuItem: MenuItemEntity? = null,

    @Column(name = "stock_item_id", length = 36, nullable = false)
    var stockItemId: String = "",

    @Column(name = "quantity_amount", precision = 19, scale = 4, nullable = false)
    var quantityAmount: BigDecimal = BigDecimal.ZERO,

    @Enumerated(EnumType.STRING)
    @Column(name = "quantity_unit", length = 20, nullable = false)
    var quantityUnit: Unit = Unit.PIECES,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now()
) {
    fun toDomain(): MenuItemIngredient {
        return MenuItemIngredient(
            stockItemId = StockItemId(stockItemId),
            quantityPerServing = Quantity(quantityAmount, quantityUnit)
        )
    }

    companion object {
        fun from(ingredient: MenuItemIngredient, menuItemEntity: MenuItemEntity): MenuItemIngredientEntity {
            return MenuItemIngredientEntity(
                id = UUID.randomUUID().toString(),
                menuItem = menuItemEntity,
                stockItemId = ingredient.stockItemId.value,
                quantityAmount = ingredient.quantityPerServing.amount,
                quantityUnit = ingredient.quantityPerServing.unit,
                createdAt = Instant.now()
            )
        }
    }
}
