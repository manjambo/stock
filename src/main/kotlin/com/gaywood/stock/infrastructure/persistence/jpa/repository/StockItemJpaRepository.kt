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
}
