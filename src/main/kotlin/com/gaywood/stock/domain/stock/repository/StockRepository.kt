package com.gaywood.stock.domain.stock.repository

import com.gaywood.stock.domain.stock.model.StockCategory
import com.gaywood.stock.domain.stock.model.StockItem
import com.gaywood.stock.domain.stock.model.StockItemId
import com.gaywood.stock.domain.stock.model.StockLocation

/**
 * Repository interface for StockItem aggregate persistence.
 * Contains only CRUD operations and basic lookups.
 * Business query logic is in [com.gaywood.stock.domain.stock.service.StockQueryService].
 */
interface StockRepository {
    fun save(stockItem: StockItem): StockItem
    fun findById(id: StockItemId): StockItem?
    fun findByLocation(location: StockLocation): List<StockItem>
    fun findByCategory(category: StockCategory): List<StockItem>
    fun findAll(): List<StockItem>
    fun delete(id: StockItemId)
}
