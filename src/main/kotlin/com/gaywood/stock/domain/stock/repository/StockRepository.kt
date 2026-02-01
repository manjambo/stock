package com.gaywood.stock.domain.stock.repository

import com.gaywood.stock.domain.stock.model.Allergen
import com.gaywood.stock.domain.stock.model.StockCategory
import com.gaywood.stock.domain.stock.model.StockItem
import com.gaywood.stock.domain.stock.model.StockItemId
import com.gaywood.stock.domain.stock.model.StockLocation

interface StockRepository {
    fun save(stockItem: StockItem): StockItem
    fun findById(id: StockItemId): StockItem?
    fun findByLocation(location: StockLocation): List<StockItem>
    fun findByCategory(category: StockCategory): List<StockItem>
    fun findByAllergen(allergen: Allergen): List<StockItem>
    fun findContainingAnyAllergen(allergens: Set<Allergen>): List<StockItem>
    fun findLowStockItems(): List<StockItem>
    fun findAll(): List<StockItem>
    fun delete(id: StockItemId)
}
