package com.gaywood.stock.infrastructure.persistence.jpa.repository

import com.gaywood.stock.domain.order.model.OrderStatus
import com.gaywood.stock.infrastructure.persistence.jpa.entity.OrderEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface OrderJpaRepository : JpaRepository<OrderEntity, String> {
    fun findByStatus(status: OrderStatus): List<OrderEntity>
    fun findByStaffId(staffId: String): List<OrderEntity>
    fun findByTableNumber(tableNumber: Int): List<OrderEntity>

    @Query("SELECT o FROM OrderEntity o WHERE o.status IN :statuses")
    fun findByStatusIn(statuses: Collection<OrderStatus>): List<OrderEntity>
}
