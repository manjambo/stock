package com.gaywood.stock.domain.stock.service

import com.gaywood.stock.domain.stock.model.Allergen
import com.gaywood.stock.domain.stock.model.StockItem
import com.gaywood.stock.domain.stock.repository.StockRepository

/**
 * Domain service for querying stock items with business logic.
 * Moves query logic from repository to domain layer where it belongs.
 */
class StockQueryService(private val stockRepository: StockRepository) {

    /**
     * Finds all stock items that are below their low stock threshold.
     */
    fun findLowStockItems(): List<StockItem> =
        stockRepository.findAll().filter { it.isLowStock() }

    /**
     * Finds all stock items containing the specified allergen.
     */
    fun findItemsWithAllergen(allergen: Allergen): List<StockItem> =
        stockRepository.findAll().filter { it.containsAllergen(allergen) }

    /**
     * Finds all stock items containing any of the specified allergens.
     */
    fun findItemsContainingAnyAllergen(allergens: Set<Allergen>): List<StockItem> =
        stockRepository.findAll().filter { item ->
            item.allergens.any { it in allergens }
        }
}
