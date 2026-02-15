package com.gaywood.stock.infrastructure.persistence.jpa.repository

import com.gaywood.stock.domain.stock.model.*
import com.gaywood.stock.domain.stock.repository.StockRepository
import com.gaywood.stock.infrastructure.persistence.jpa.converters.StockCategoryConverter
import com.gaywood.stock.infrastructure.persistence.jpa.entity.StockItemEntity
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class JpaStockRepositoryAdapter(
    private val jpaRepository: StockItemJpaRepository
) : StockRepository {

    override fun save(stockItem: StockItem): StockItem {
        val entity = jpaRepository.findById(stockItem.id.value).orElse(null)
            ?.let { updateEntity(it, stockItem) }
            ?: StockItemEntity.from(stockItem)
        return jpaRepository.save(entity).toDomain()
    }

    override fun findById(id: StockItemId): StockItem? {
        return jpaRepository.findById(id.value).orElse(null)?.toDomain()
    }

    override fun findByLocation(location: StockLocation): List<StockItem> {
        return jpaRepository.findByLocation(location).map { it.toDomain() }
    }

    override fun findByCategory(category: StockCategory): List<StockItem> {
        val categoryValue = StockCategoryConverter.toDbValue(category)
        return jpaRepository.findByCategoryValue(categoryValue).map { it.toDomain() }
    }

    override fun findAll(): List<StockItem> {
        return jpaRepository.findAll().map { it.toDomain() }
    }

    override fun delete(id: StockItemId) {
        jpaRepository.deleteById(id.value)
    }

    override fun findLowStockItems(): List<StockItem> {
        return jpaRepository.findLowStockItems().map { it.toDomain() }
    }

    override fun findByAllergen(allergen: Allergen): List<StockItem> {
        return jpaRepository.findByAllergensContaining(allergen).map { it.toDomain() }
    }

    override fun findByAnyAllergen(allergens: Set<Allergen>): List<StockItem> {
        if (allergens.isEmpty()) return emptyList()
        return jpaRepository.findByAllergensIn(allergens).map { it.toDomain() }
    }

    private fun updateEntity(existing: StockItemEntity, stockItem: StockItem): StockItemEntity {
        existing.updateFrom(stockItem)
        return existing
    }
}
