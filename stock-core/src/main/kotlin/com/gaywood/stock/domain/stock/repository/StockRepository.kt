package com.gaywood.stock.domain.stock.repository

import com.gaywood.stock.domain.stock.model.Allergen
import com.gaywood.stock.domain.stock.model.StockCategory
import com.gaywood.stock.domain.stock.model.StockItem
import com.gaywood.stock.domain.stock.model.StockItemId
import com.gaywood.stock.domain.stock.model.StockLocation

/**
 * Repository interface for StockItem aggregate persistence.
 * Contains CRUD operations and optimized queries.
 * Business query logic is in [com.gaywood.stock.domain.stock.service.StockQueryService].
 */
interface StockRepository {
    fun save(stockItem: StockItem): StockItem
    fun findById(id: StockItemId): StockItem?
    fun findByLocation(location: StockLocation): List<StockItem>
    fun findByCategory(category: StockCategory): List<StockItem>
    fun findAll(): List<StockItem>
    fun delete(id: StockItemId)

    /**
     * Finds all stock items where quantity is at or below the low stock threshold.
     * More efficient than loading all items and filtering in memory.
     */
    fun findLowStockItems(): List<StockItem>

    /**
     * Finds all stock items containing the specified allergen.
     */
    fun findByAllergen(allergen: Allergen): List<StockItem>

    /**
     * Finds all stock items containing any of the specified allergens.
     */
    fun findByAnyAllergen(allergens: Set<Allergen>): List<StockItem>
}
