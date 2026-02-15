package com.gaywood.stock.infrastructure.persistence.jpa.repository

import com.gaywood.stock.domain.stock.model.Allergen
import com.gaywood.stock.domain.stock.model.StockLocation
import com.gaywood.stock.infrastructure.persistence.jpa.entity.StockItemEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface StockItemJpaRepository : JpaRepository<StockItemEntity, String> {
    fun findByLocation(location: StockLocation): List<StockItemEntity>

    @Query("SELECT s FROM StockItemEntity s WHERE :allergen MEMBER OF s.allergens")
    fun findByAllergensContaining(allergen: Allergen): List<StockItemEntity>

    @Query("SELECT DISTINCT s FROM StockItemEntity s JOIN s.allergens a WHERE a IN :allergens")
    fun findByAllergensIn(allergens: Collection<Allergen>): List<StockItemEntity>

    /**
     * Finds items where quantity is at or below the low stock threshold.
     * Only includes items that have a threshold set (not null).
     * Uses same unit comparison - items with mismatched units are excluded.
     */
    @Query("""
        SELECT s FROM StockItemEntity s
        WHERE s.lowStockThresholdAmount IS NOT NULL
        AND s.lowStockThresholdUnit IS NOT NULL
        AND s.quantityUnit = s.lowStockThresholdUnit
        AND s.quantityAmount <= s.lowStockThresholdAmount
    """)
    fun findLowStockItems(): List<StockItemEntity>

    /**
     * Finds items by category using native query since category is stored as String via converter.
     */
    @Query("SELECT * FROM stock_items s WHERE s.category = :category", nativeQuery = true)
    fun findByCategoryValue(@org.springframework.data.repository.query.Param("category") category: String): List<StockItemEntity>
}
