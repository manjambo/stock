package com.gaywood.stock.domain.stock.event

import com.gaywood.stock.domain.shared.DomainEvent
import com.gaywood.stock.domain.stock.model.Allergen
import com.gaywood.stock.domain.stock.model.Quantity
import com.gaywood.stock.domain.stock.model.StockItemId
import java.time.Instant

sealed class StockEvent : DomainEvent {
    abstract val stockItemId: StockItemId

    data class StockAdded(
        override val stockItemId: StockItemId,
        val quantityAdded: Quantity,
        val newTotal: Quantity,
        override val occurredAt: Instant = Instant.now()
    ) : StockEvent()

    data class StockRemoved(
        override val stockItemId: StockItemId,
        val quantityRemoved: Quantity,
        val newTotal: Quantity,
        override val occurredAt: Instant = Instant.now()
    ) : StockEvent()

    data class StockAdjusted(
        override val stockItemId: StockItemId,
        val previousQuantity: Quantity,
        val newQuantity: Quantity,
        val reason: String,
        override val occurredAt: Instant = Instant.now()
    ) : StockEvent()

    data class LowStockAlertRaised(
        override val stockItemId: StockItemId,
        val itemName: String,
        val currentQuantity: Quantity,
        val threshold: Quantity,
        override val occurredAt: Instant = Instant.now()
    ) : StockEvent()

    data class ThresholdUpdated(
        override val stockItemId: StockItemId,
        val previousThreshold: Quantity?,
        val newThreshold: Quantity,
        override val occurredAt: Instant = Instant.now()
    ) : StockEvent()

    data class AllergensUpdated(
        override val stockItemId: StockItemId,
        val itemName: String,
        val allergens: Set<Allergen>,
        override val occurredAt: Instant = Instant.now()
    ) : StockEvent()
}
