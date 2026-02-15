package com.gaywood.stock.infrastructure.persistence.jpa.repository

import com.gaywood.stock.domain.order.model.Order
import com.gaywood.stock.domain.order.model.OrderId
import com.gaywood.stock.domain.order.model.OrderStatus
import com.gaywood.stock.domain.order.repository.OrderRepository
import com.gaywood.stock.domain.staff.model.StaffId
import com.gaywood.stock.infrastructure.persistence.jpa.entity.OrderEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class JpaOrderRepositoryAdapter(
    private val jpaRepository: OrderJpaRepository
) : OrderRepository {

    companion object {
        private val ACTIVE_STATUSES = listOf(
            OrderStatus.PENDING,
            OrderStatus.IN_PROGRESS,
            OrderStatus.READY,
            OrderStatus.SERVED
        )
    }

    override fun save(order: Order): Order {
        val entity = jpaRepository.findById(order.id.value).orElse(null)
            ?.let { updateEntity(it, order) }
            ?: OrderEntity.from(order)
        return jpaRepository.save(entity).toDomain()
    }

    @Transactional(readOnly = true)
    override fun findById(id: OrderId): Order? {
        return jpaRepository.findById(id.value).orElse(null)?.toDomain()
    }

    @Transactional(readOnly = true)
    override fun findByStatus(status: OrderStatus): List<Order> {
        return jpaRepository.findByStatusWithItems(status).map { it.toDomain() }
    }

    @Transactional(readOnly = true)
    override fun findByStaffId(staffId: StaffId): List<Order> {
        return jpaRepository.findByStaffIdWithItems(staffId.value).map { it.toDomain() }
    }

    @Transactional(readOnly = true)
    override fun findByTableNumber(tableNumber: Int): List<Order> {
        return jpaRepository.findByTableNumberWithItems(tableNumber).map { it.toDomain() }
    }

    @Transactional(readOnly = true)
    override fun findActiveOrders(): List<Order> {
        return jpaRepository.findByStatusInWithItems(ACTIVE_STATUSES).map { it.toDomain() }
    }

    @Transactional(readOnly = true)
    override fun findActiveOrders(pageable: Pageable): Page<Order> {
        return jpaRepository.findByStatusInWithItems(ACTIVE_STATUSES, pageable).map { it.toDomain() }
    }

    @Transactional(readOnly = true)
    override fun findByTableNumber(tableNumber: Int, pageable: Pageable): Page<Order> {
        return jpaRepository.findByTableNumberWithItems(tableNumber, pageable).map { it.toDomain() }
    }

    @Transactional(readOnly = true)
    override fun findAll(): List<Order> {
        return jpaRepository.findAll().map { it.toDomain() }
    }

    override fun delete(id: OrderId) {
        jpaRepository.deleteById(id.value)
    }

    private fun updateEntity(existing: OrderEntity, order: Order): OrderEntity {
        existing.updateFrom(order)
        return existing
    }
}
