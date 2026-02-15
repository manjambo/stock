package com.gaywood.stock.infrastructure.persistence.jpa.repository

import com.gaywood.stock.infrastructure.persistence.jpa.entity.StockEventEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StockEventJpaRepository : JpaRepository<StockEventEntity, Long> {
    fun findByStockItemIdOrderByOccurredAtAsc(stockItemId: String): List<StockEventEntity>
    fun findAllByOrderByOccurredAtAsc(): List<StockEventEntity>
}
