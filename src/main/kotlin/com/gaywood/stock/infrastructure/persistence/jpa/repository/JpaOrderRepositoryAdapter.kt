package com.gaywood.stock.infrastructure.persistence.jpa.repository

import com.gaywood.stock.domain.order.model.Order
import com.gaywood.stock.domain.order.model.OrderId
import com.gaywood.stock.domain.order.model.OrderStatus
import com.gaywood.stock.domain.order.repository.OrderRepository
import com.gaywood.stock.domain.staff.model.StaffId
import com.gaywood.stock.infrastructure.persistence.jpa.entity.OrderEntity
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class JpaOrderRepositoryAdapter(
    private val jpaRepository: OrderJpaRepository
) : OrderRepository {

    override fun save(order: Order): Order {
        val entity = jpaRepository.findById(order.id.value).orElse(null)
            ?.let { updateEntity(it, order) }
            ?: OrderEntity.from(order)
        return jpaRepository.save(entity).toDomain()
    }

    override fun findById(id: OrderId): Order? {
        return jpaRepository.findById(id.value).orElse(null)?.toDomain()
    }

    override fun findByStatus(status: OrderStatus): List<Order> {
        return jpaRepository.findByStatus(status).map { it.toDomain() }
    }

    override fun findByStaffId(staffId: StaffId): List<Order> {
        return jpaRepository.findByStaffId(staffId.value).map { it.toDomain() }
    }

    override fun findByTableNumber(tableNumber: Int): List<Order> {
        return jpaRepository.findByTableNumber(tableNumber).map { it.toDomain() }
    }

    override fun findActiveOrders(): List<Order> {
        val activeStatuses = listOf(
            OrderStatus.PENDING,
            OrderStatus.IN_PROGRESS,
            OrderStatus.READY,
            OrderStatus.SERVED
        )
        return jpaRepository.findByStatusIn(activeStatuses).map { it.toDomain() }
    }

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
