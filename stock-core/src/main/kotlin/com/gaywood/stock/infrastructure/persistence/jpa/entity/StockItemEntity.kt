package com.gaywood.stock.infrastructure.persistence.jpa.entity

import com.gaywood.stock.domain.stock.model.Allergen
import com.gaywood.stock.domain.stock.model.LowStockThreshold
import com.gaywood.stock.domain.stock.model.Quantity
import com.gaywood.stock.domain.stock.model.StockCategory
import com.gaywood.stock.domain.stock.model.StockItem
import com.gaywood.stock.domain.stock.model.StockItemId
import com.gaywood.stock.domain.stock.model.StockLocation
import com.gaywood.stock.domain.stock.model.Unit as StockUnit
import com.gaywood.stock.infrastructure.persistence.jpa.converters.StockCategoryConverter
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "stock_items")
class StockItemEntity(
    @Id
    @Column(name = "id", length = 36)
    var id: String = "",

    @Column(name = "item_name", length = 255, nullable = false)
    var name: String = "",

    @Column(name = "category", length = 50, nullable = false)
    @Convert(converter = StockCategoryConverter::class)
    var category: StockCategory = StockCategory.Bar.Spirits,

    @Column(name = "location", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    var location: StockLocation = StockLocation.BAR,

    @Column(name = "quantity_amount", precision = 19, scale = 4, nullable = false)
    var quantityAmount: BigDecimal = BigDecimal.ZERO,

    @Column(name = "quantity_unit", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    var quantityUnit: StockUnit = StockUnit.PIECES,

    @Column(name = "low_stock_threshold_amount", precision = 19, scale = 4)
    var lowStockThresholdAmount: BigDecimal? = null,

    @Column(name = "low_stock_threshold_unit", length = 20)
    @Enumerated(EnumType.STRING)
    var lowStockThresholdUnit: StockUnit? = null,

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "stock_item_allergens",
        joinColumns = [JoinColumn(name = "stock_item_id")]
    )
    @Column(name = "allergen", length = 20)
    @Enumerated(EnumType.STRING)
    var allergens: MutableSet<Allergen> = mutableSetOf(),

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
) {
    fun toDomain(): StockItem {
        val threshold = lowStockThresholdAmount?.let { amount ->
            lowStockThresholdUnit?.let { unit ->
                LowStockThreshold(Quantity(amount, unit))
            }
        }

        return StockItem.create(
            id = StockItemId(id),
            name = name,
            category = category,
            initialQuantity = Quantity(quantityAmount, quantityUnit),
            lowStockThreshold = threshold,
            allergens = allergens.toSet()
        )
    }

    fun updateFrom(stockItem: StockItem) {
        name = stockItem.name
        category = stockItem.category
        location = stockItem.location
        quantityAmount = stockItem.quantity.amount
        quantityUnit = stockItem.quantity.unit
        lowStockThresholdAmount = stockItem.lowStockThreshold?.quantity?.amount
        lowStockThresholdUnit = stockItem.lowStockThreshold?.quantity?.unit
        replaceAllergens(stockItem.allergens)
    }

    private fun replaceAllergens(newAllergens: Set<Allergen>) {
        allergens.clear()
        allergens.addAll(newAllergens)
    }

    @PreUpdate
    fun preUpdate() {
        updatedAt = Instant.now()
    }

    companion object {
        fun from(stockItem: StockItem): StockItemEntity {
            return StockItemEntity(
                id = stockItem.id.value,
                name = stockItem.name,
                category = stockItem.category,
                location = stockItem.location,
                quantityAmount = stockItem.quantity.amount,
                quantityUnit = stockItem.quantity.unit,
                lowStockThresholdAmount = stockItem.lowStockThreshold?.quantity?.amount,
                lowStockThresholdUnit = stockItem.lowStockThreshold?.quantity?.unit,
                allergens = stockItem.allergens.toMutableSet(),
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )
        }
    }
}
