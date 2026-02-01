package com.gaywood.stock.infrastructure.persistence.jpa.entity

import com.gaywood.stock.domain.order.model.Order
import com.gaywood.stock.domain.order.model.OrderId
import com.gaywood.stock.domain.order.model.OrderStatus
import com.gaywood.stock.domain.staff.model.StaffId
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "orders")
class OrderEntity(
    @Id
    @Column(name = "id", length = 36)
    var id: String = "",

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    var status: OrderStatus = OrderStatus.PENDING,

    @Column(name = "table_number")
    var tableNumber: Int? = null,

    @Column(name = "staff_id", length = 36, nullable = false)
    var staffId: String = "",

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    var items: MutableList<OrderItemEntity> = mutableListOf()
) {
    fun toDomain(): Order {
        return Order.reconstitute(
            id = OrderId(id),
            items = items.map { it.toDomain() },
            status = status,
            tableNumber = tableNumber,
            staffId = StaffId(staffId),
            createdAt = createdAt
        )
    }

    fun updateFrom(order: Order) {
        status = order.status
        tableNumber = order.tableNumber
        staffId = order.staffId.value
        replaceItemsWith(order.items)
    }

    private fun replaceItemsWith(newItems: List<com.gaywood.stock.domain.order.model.OrderItem>) {
        items.clear()
        newItems.forEach { item ->
            items.add(OrderItemEntity.from(item, this))
        }
    }

    @PreUpdate
    fun preUpdate() {
        updatedAt = Instant.now()
    }

    companion object {
        fun from(order: Order): OrderEntity {
            val entity = OrderEntity(
                id = order.id.value,
                status = order.status,
                tableNumber = order.tableNumber,
                staffId = order.staffId.value,
                createdAt = order.createdAt,
                updatedAt = Instant.now()
            )
            entity.items = order.items.map { item ->
                OrderItemEntity.from(item, entity)
            }.toMutableList()
            return entity
        }
    }
}
