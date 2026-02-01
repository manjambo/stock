package com.gaywood.stock.infrastructure.persistence.jpa.entity

import com.gaywood.stock.domain.menu.model.MenuItem
import com.gaywood.stock.domain.menu.model.MenuItemId
import com.gaywood.stock.domain.menu.model.Price
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant
import java.util.Currency

@Entity
@Table(name = "menu_items")
class MenuItemEntity(
    @Id
    @Column(name = "id", length = 36)
    var id: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    var menu: MenuEntity? = null,

    @Column(name = "name", length = 255, nullable = false)
    var name: String = "",

    @Column(name = "description", columnDefinition = "TEXT")
    var description: String = "",

    @Column(name = "price_amount", precision = 19, scale = 4, nullable = false)
    var priceAmount: BigDecimal = BigDecimal.ZERO,

    @Column(name = "price_currency", length = 3, nullable = false)
    var priceCurrency: String = "GBP",

    @Column(name = "available", nullable = false)
    var available: Boolean = true,

    @OneToMany(mappedBy = "menuItem", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    var ingredients: MutableList<MenuItemIngredientEntity> = mutableListOf(),

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
) {
    fun toDomain(): MenuItem {
        return MenuItem.create(
            id = MenuItemId(id),
            name = name,
            description = description,
            price = Price(priceAmount, Currency.getInstance(priceCurrency)),
            ingredients = ingredients.map { it.toDomain() },
            available = available
        )
    }

    @PreUpdate
    fun preUpdate() {
        updatedAt = Instant.now()
    }

    companion object {
        fun from(item: MenuItem, menuEntity: MenuEntity): MenuItemEntity {
            val entity = MenuItemEntity(
                id = item.id.value,
                menu = menuEntity,
                name = item.name,
                description = item.description,
                priceAmount = item.price.amount,
                priceCurrency = item.price.currency.currencyCode,
                available = item.available,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )
            entity.ingredients = item.ingredients.map { ingredient ->
                MenuItemIngredientEntity.from(ingredient, entity)
            }.toMutableList()
            return entity
        }
    }
}
