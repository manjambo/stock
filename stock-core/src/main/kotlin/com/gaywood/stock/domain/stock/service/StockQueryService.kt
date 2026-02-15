package com.gaywood.stock.domain.stock.service

import com.gaywood.stock.domain.stock.model.Allergen
import com.gaywood.stock.domain.stock.model.StockItem
import com.gaywood.stock.domain.stock.repository.StockRepository

/**
 * Domain service for querying stock items with business logic.
 * Delegates to repository for efficient database queries.
 */
class StockQueryService(private val stockRepository: StockRepository) {

    /**
     * Finds all stock items that are below their low stock threshold.
     * Uses an optimized database query instead of loading all items.
     */
    fun findLowStockItems(): List<StockItem> =
        stockRepository.findLowStockItems()

    /**
     * Finds all stock items containing the specified allergen.
     * Uses an optimized database query instead of loading all items.
     */
    fun findItemsWithAllergen(allergen: Allergen): List<StockItem> =
        stockRepository.findByAllergen(allergen)

    /**
     * Finds all stock items containing any of the specified allergens.
     * Uses an optimized database query instead of loading all items.
     */
    fun findItemsContainingAnyAllergen(allergens: Set<Allergen>): List<StockItem> =
        stockRepository.findByAnyAllergen(allergens)
}
