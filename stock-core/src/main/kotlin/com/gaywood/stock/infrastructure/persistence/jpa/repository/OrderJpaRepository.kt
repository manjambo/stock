package com.gaywood.stock.infrastructure.persistence.jpa.repository

import com.gaywood.stock.domain.order.model.OrderStatus
import com.gaywood.stock.infrastructure.persistence.jpa.entity.OrderEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface OrderJpaRepository : JpaRepository<OrderEntity, String> {

    @EntityGraph(attributePaths = ["items"])
    override fun findById(id: String): Optional<OrderEntity>

    @EntityGraph(attributePaths = ["items"])
    @Query("SELECT o FROM OrderEntity o WHERE o.status = :status")
    fun findByStatusWithItems(status: OrderStatus): List<OrderEntity>

    @EntityGraph(attributePaths = ["items"])
    @Query("SELECT o FROM OrderEntity o WHERE o.staffId = :staffId")
    fun findByStaffIdWithItems(staffId: String): List<OrderEntity>

    @EntityGraph(attributePaths = ["items"])
    @Query("SELECT o FROM OrderEntity o WHERE o.tableNumber = :tableNumber")
    fun findByTableNumberWithItems(tableNumber: Int): List<OrderEntity>

    @EntityGraph(attributePaths = ["items"])
    @Query("SELECT o FROM OrderEntity o WHERE o.status IN :statuses")
    fun findByStatusInWithItems(statuses: Collection<OrderStatus>): List<OrderEntity>

    @EntityGraph(attributePaths = ["items"])
    @Query("SELECT o FROM OrderEntity o WHERE o.status IN :statuses")
    fun findByStatusInWithItems(statuses: Collection<OrderStatus>, pageable: Pageable): Page<OrderEntity>

    @EntityGraph(attributePaths = ["items"])
    @Query("SELECT o FROM OrderEntity o WHERE o.tableNumber = :tableNumber")
    fun findByTableNumberWithItems(tableNumber: Int, pageable: Pageable): Page<OrderEntity>
}
